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

    private val systemPrompt = """
        You are Sophia, an AI operating assistant living inside an Android app called Sophia OS.
        You are warm, capable, and concise. You help the user with whatever they ask —
        answering questions, thinking through problems, drafting things, and keeping track
        of what matters to them. Speak naturally and conversationally. Keep replies focused
        and not overly long unless the user asks for depth.
    """.trimIndent()

    suspend fun generateReply(history: List<PersistedMessage>): Result<String> = withContext(Dispatchers.IO) {
        try {
            val messagesArray = JSONArray()
            history.forEach { message ->
                val obj = JSONObject()
                obj.put("role", if (message.fromUser) "user" else "assistant")
                obj.put("content", message.text)
                messagesArray.put(obj)
            }

            val payload = JSONObject().apply {
                put("model", "claude-sonnet-4-6")
                put("max_tokens", 1024)
                put("system", systemPrompt)
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
                val json = JSONObject(body)
                val contentArray = json.getJSONArray("content")
                val text = StringBuilder()
                for (i in 0 until contentArray.length()) {
                    val block = contentArray.getJSONObject(i)
                    if (block.getString("type") == "text") {
                        text.append(block.getString("text"))
                    }
                }
                Result.success(text.toString().trim())
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    private fun extractErrorMessage(body: String): String = try {
        JSONObject(body).getJSONObject("error").getString("message")
    } catch (_: Throwable) {
        body.take(200)
    }
}
