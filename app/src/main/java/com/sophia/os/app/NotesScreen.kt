package com.sophia.os.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NInk = Color(0xFF050507)
private val NPanel = Color(0xFF121016)
private val NInputBg = Color(0xFF1A1720)
private val NGold = Color(0xFFD4AF37)
private val NGoldBright = Color(0xFFFFC94D)
private val NGoldDim = Color(0xFF8A7223)
private val NTextPrimary = Color(0xFFF5F2E8)
private val NTextMuted = Color(0xFF9A927E)

@Composable
fun NotesListScreen(
    notes: List<Note>,
    onOpen: (Long) -> Unit,
    onNew: () -> Unit,
    onDelete: (Long) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NInk, Color(0xFF0C0A0F), NInk)))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NPanel)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("←", color = NTextPrimary, fontSize = 20.sp)
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text("NOTES", color = NGold, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(
                    "${notes.size} ${if (notes.size == 1) "note" else "notes"}",
                    color = NTextMuted,
                    fontSize = 12.sp,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.horizontalGradient(listOf(NGoldDim, NGold, NGoldBright)))
                .clickable { onNew() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("+  New Note", color = NInk, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        if (notes.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("No notes yet", color = NTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "Tap New Note to jot something down.",
                    color = NTextMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteRow(note = note, onOpen = { onOpen(note.id) }, onDelete = { onDelete(note.id) })
                }
            }
        }
    }
}

@Composable
private fun NoteRow(note: Note, onOpen: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NPanel)
            .clickable { onOpen() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                note.title.ifBlank { "Untitled" },
                color = NTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
            val preview = note.body.ifBlank { "No additional text" }.take(60)
            Text(preview, color = NTextMuted, fontSize = 12.sp, maxLines = 1, modifier = Modifier.padding(top = 2.dp))
        }
        Spacer(Modifier.size(12.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(NInputBg)
                .clickable { onDelete() },
            contentAlignment = Alignment.Center,
        ) {
            Text("✕", color = NTextMuted, fontSize = 13.sp)
        }
    }
}

@Composable
fun NoteEditorScreen(
    initialTitle: String,
    initialBody: String,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit,
) {
    var title by remember { mutableStateOf(initialTitle) }
    var body by remember { mutableStateOf(initialBody) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NInk, Color(0xFF0C0A0F), NInk)))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NPanel)
                    .clickable {
                        onSave(title, body)
                        onBack()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("←", color = NTextPrimary, fontSize = 20.sp)
            }
            Text(
                "EDIT NOTE",
                color = NGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 12.dp).weight(1f),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(NGold, NGoldBright)))
                    .clickable { onSave(title, body) }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Save", color = NInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        TextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(14.dp)),
            placeholder = { Text("Title", color = NTextMuted, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
            textStyle = LocalTextStyle.current.copy(color = NTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NInputBg,
                unfocusedContainerColor = NInputBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = NGold,
            ),
        )

        TextField(
            value = body,
            onValueChange = { body = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(14.dp)),
            placeholder = { Text("Start writing…", color = NTextMuted) },
            textStyle = LocalTextStyle.current.copy(color = NTextPrimary, fontSize = 15.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NInputBg,
                unfocusedContainerColor = NInputBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = NGold,
            ),
        )

        Spacer(Modifier.size(12.dp))
    }
}
