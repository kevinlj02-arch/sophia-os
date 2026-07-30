package com.sophia.os.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val EmblemGold = Color(0xFFD4AF37)
private val EmblemGoldBright = Color(0xFFFFC94D)
private val EmblemInk = Color(0xFF0C0A0F)

@Composable
fun SophiaEmblem(modifier: Modifier = Modifier, emblemSize: Dp = 32.dp) {
    Canvas(modifier = modifier.size(emblemSize)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(EmblemGoldBright, EmblemGold),
                center = Offset(center.x - r * 0.2f, center.y - r * 0.2f),
                radius = r,
            ),
            radius = r,
            center = center,
        )

        val w = size.width
        val h = size.height
        val s = Path().apply {
            moveTo(w * 0.66f, h * 0.36f)
            cubicTo(w * 0.66f, h * 0.28f, w * 0.40f, h * 0.26f, w * 0.38f, h * 0.42f)
            cubicTo(w * 0.36f, h * 0.54f, w * 0.64f, h * 0.50f, w * 0.62f, h * 0.62f)
            cubicTo(w * 0.60f, h * 0.74f, w * 0.36f, h * 0.72f, w * 0.34f, h * 0.64f)
        }
        drawPath(
            path = s,
            color = EmblemInk,
            style = Stroke(width = r * 0.22f),
        )
    }
}
