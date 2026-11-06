package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CalculationResult
import com.example.domain.Coordinates
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun OscSchematicCanvas(
    origin: Coordinates,
    target: Coordinates,
    result: CalculationResult,
    modifier: Modifier = Modifier
) {
    // Pulse animation for radar beam and target reticle
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TargetPulseAlpha"
    )

    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarSweep"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xEE0B0C15))
            .border(1.dp, GlassBorderRed, RoundedCornerShape(16.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = min(size.width, size.height) / 2f * 0.85f

            // 1. Concentric radar rings
            val ringColors = listOf(0.25f, 0.5f, 0.75f, 1.0f)
            ringColors.forEach { frac ->
                drawCircle(
                    color = LaserCyan.copy(alpha = 0.15f),
                    radius = radius * frac,
                    center = center,
                    style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
                )
            }

            // 2. Cardinal Crosshairs
            drawLine(
                color = LaserCyan.copy(alpha = 0.25f),
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = LaserCyan.copy(alpha = 0.25f),
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y + radius),
                strokeWidth = 1.dp.toPx()
            )

            // 3. Radar Sweep Line
            rotate(degrees = sweepAngle, pivot = center) {
                drawLine(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonRed.copy(alpha = 0.6f), Color.Transparent),
                        center = center,
                        radius = radius
                    ),
                    start = center,
                    end = Offset(center.x, center.y - radius),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // 4. Calculate Vector to Target
            val dx = target.x - origin.x
            val dz = target.z - origin.z
            val distance = sqrt(dx * dx + dz * dz)

            // In Minecraft, North is -Z (up on screen), South is +Z (down on screen)
            // East is +X (right on screen), West is -X (left on screen)
            // Screen coordinates:
            // screenX = center.x + (dx / distance) * targetRadius
            // screenY = center.y + (dz / distance) * targetRadius
            val targetDistFraction = 0.78f
            val targetOffset = if (distance > 0.0) {
                val unitX = (dx / distance).toFloat()
                val unitZ = (dz / distance).toFloat()
                Offset(center.x + unitX * radius * targetDistFraction, center.y + unitZ * radius * targetDistFraction)
            } else {
                center
            }

            // 5. Azimuth Arc (From North = 0° clockwise to target)
            val azimuthDeg = result.azimuthDegrees.toFloat()
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color.Transparent, NeonRed.copy(alpha = 0.4f)),
                    center = center
                ),
                startAngle = -90f,
                sweepAngle = azimuthDeg,
                useCenter = true
            )

            // 6. Trajectory line from Cannon Origin to Target
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(LaserCyan, NeonRed),
                    start = center,
                    end = targetOffset
                ),
                start = center,
                end = targetOffset,
                strokeWidth = 2.5.dp.toPx()
            )

            // Trajectory dash pulse
            drawCircle(
                color = NeonRedBright.copy(alpha = pulseAlpha),
                radius = 6.dp.toPx(),
                center = targetOffset,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = NeonRed,
                radius = 3.dp.toPx(),
                center = targetOffset
            )

            // 7. Cannon Origin Icon at center
            drawCircle(
                color = LaserCyan,
                radius = 5.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = center
            )
        }

        // HUD Overlay Labels
        // Cardinal direction markers
        Text(
            text = "N (0°)",
            color = LaserCyan.copy(alpha = 0.8f),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 6.dp)
        )
        Text(
            text = "S (180°)",
            color = LaserCyan.copy(alpha = 0.8f),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
        )
        Text(
            text = "E (+X)",
            color = LaserCyan.copy(alpha = 0.8f),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp)
        )
        Text(
            text = "W (-X)",
            color = LaserCyan.copy(alpha = 0.8f),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 6.dp)
        )

        // Telemetry Box at top-left
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0x99000000))
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = "AZM: ${"%.1f".format(result.azimuthDegrees)}°",
                color = NeonRedBright,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Text(
                text = "DST: ${"%,.0f".format(result.distance)}m",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        }

        // Target coordinates at bottom-right
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0x99000000))
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "ΔX: %+.0f".format(target.x - origin.x),
                color = TextGraySecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
            Text(
                text = "ΔZ: %+.0f".format(target.z - origin.z),
                color = TextGraySecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
            Text(
                text = "ΔY: %+.0f".format(target.y - origin.y),
                color = LaserCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        }
    }
}
