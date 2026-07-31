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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TInk = Color(0xFF050507)
private val TPanel = Color(0xFF121016)
private val TInputBg = Color(0xFF1A1720)
private val TGold = Color(0xFFD4AF37)
private val TGoldBright = Color(0xFFFFC94D)
private val TGoldDim = Color(0xFF8A7223)
private val TTextPrimary = Color(0xFFF5F2E8)
private val TTextMuted = Color(0xFF9A927E)
private val TGreen = Color(0xFF4ADE80)

@Composable
fun TaskScreen(
    tasks: List<Task>,
    draft: String,
    onDraftChange: (String) -> Unit,
    onAdd: () -> Unit,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onClearCompleted: () -> Unit,
    onBack: () -> Unit,
) {
    val remaining = tasks.count { !it.done }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(TInk, Color(0xFF0C0A0F), TInk)))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TPanel)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("←", color = TTextPrimary, fontSize = 20.sp)
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("TASK MANAGER", color = TGold, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(
                    "$remaining active · ${tasks.size} total",
                    color = TTextMuted,
                    fontSize = 12.sp,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)),
                placeholder = { Text("Add a task…", color = TTextMuted) },
                textStyle = LocalTextStyle.current.copy(color = TTextPrimary, fontSize = 15.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = TInputBg,
                    unfocusedContainerColor = TInputBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = TGold,
                ),
            )
            Spacer(Modifier.size(10.dp))
            val addActive = draft.isNotBlank()
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (addActive) Brush.horizontalGradient(listOf(TGold, TGoldBright))
                        else Brush.horizontalGradient(listOf(TPanel, TPanel))
                    )
                    .clickable(enabled = addActive) { onAdd() },
                contentAlignment = Alignment.Center,
            ) {
                Text("+", color = if (addActive) TInk else TTextMuted, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (tasks.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("No tasks yet", color = TTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "Add something above and Sophia will keep track of it for you.",
                    color = TTextMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(task = task, onToggle = { onToggle(task.id) }, onDelete = { onDelete(task.id) })
                }
            }
            if (tasks.any { it.done }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TPanel)
                        .clickable { onClearCompleted() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Clear completed", color = TGoldDim, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(TPanel)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(if (task.done) Brush.linearGradient(listOf(TGold, TGoldBright)) else Brush.linearGradient(listOf(TInputBg, TInputBg)))
                .clickable { onToggle() },
            contentAlignment = Alignment.Center,
        ) {
            if (task.done) Text("✓", color = TInk, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.size(12.dp))
        Text(
            task.text,
            color = if (task.done) TTextMuted else TTextPrimary,
            fontSize = 15.sp,
            textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.size(12.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TInputBg)
                .clickable { onDelete() },
            contentAlignment = Alignment.Center,
        ) {
            Text("✕", color = TTextMuted, fontSize = 13.sp)
        }
    }
}
