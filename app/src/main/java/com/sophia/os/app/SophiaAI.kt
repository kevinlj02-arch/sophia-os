package com.sophia.os.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SophiaAI(private val apiKey: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun baseSystemPrompt(knownFacts: String): String {
        val memorySection = if (knownFacts.isBlank()) {
            "You don't have any stored facts about this user yet."
        } else {
            "Here is what you remember about this user from past conversations:\n$knownFacts"
        }
        return """
            You are Sophia, an AI operating assistant living inside an Android app called Sophia OS.
            You are warm, capable, and concise. You help the user with whatever they ask —
            answering questions, thinking through problems, drafting things, and keeping track
            of what matters to them. Speak naturally and conversationally. Keep replies focused
            and not overly long unless the user asks for depth.

            $memorySection

            Use what you remember naturally when relevant, but don't recite it back mechanically.
        """.trimIndent()
    }

    suspend fun generateReply(
        history: List<PersistedMessage>,
        knownFacts: String = "",
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val messagesArray = JSONArray()
            history.forEach { message ->
                messagesArray.put(JSONObject().apply {
                    put("role", if (message.fromUser) "user" else "assistant")
                    put("content", message.text)
                })
            }

            val payload = JSONObject().apply {
                put("model", "claude-sonnet-4-6")
                put("max_tokens", 1024)
                put("system", baseSystemPrompt(knownFacts))
                put("messages", messagesArray)
            }

            val request = Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception("API error ${response.code}: ${extractErrorMessage(body)}")
                    )
                }
                Result.success(extractText(body))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    suspend fun extractFacts(
        userMessage: String,
        sophiaReply: String,
    ): List<String> = withContext(Dispatchers.IO) {
        try {
            val instruction = """
                From the following exchange, extract any durable facts worth remembering
                about the user long-term (preferences, personal details, ongoing projects,
                important context). Ignore small talk and transient details.

                Respond ONLY with a JSON array of short fact strings, nothing else.
                If there's nothing worth remembering, respond with [].

                User said: "$userMessage"
                You replied: "$sophiaReply"
            """.trimIndent()

            val payload = JSONObject().apply {
                put("model", "claude-sonnet-4-6")
                put("max_tokens", 512)
                put("messages", JSONArray().put(JSONObject().apply {
                    put("role", "user")
                    put("content", instruction)
                }))
            }

            val request = Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val text = extractText(response.body?.string().orEmpty()).trim()
                parseFactArray(text)
            }
        } catch (t: Throwable) {
            emptyList()
        }
    }

    private fun parseFactArray(text: String): List<String> = try {
        val cleaned = text.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val array = JSONArray(cleaned)
        (0 until array.length()).mapNotNull { i ->
            array.optString(i, "").trim().takeIf { it.isNotEmpty() }
        }
    } catch (t: Throwable) {
        emptyList()
    }

    private fun extractText(body: String): String {
        val json = JSONObject(body)
        val contentArray = json.getJSONArray("content")
        val sb = StringBuilder()
        for (i in 0 until contentArray.length()) {
            val block = contentArray.getJSONObject(i)
            if (block.getString("type") == "text") sb.append(block.getString("text"))
        }
        return sb.toString().trim()
    }

    private fun extractErrorMessage(body: String): String = try {
        JSONObject(body).getJSONObject("error").getString("message")
    } catch (_: Throwable) {
        body.take(200)
    }
}
