package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Ultra-realistic liquid glass card.
 * Features:
 * - Milky frosted translucent white body gradient
 * - Specular reflection shine on the top edge
 * - Thin glowing neon red edge accents
 * - Soft volumetric drop shadow with red ambient aura
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderColor: Color = LiquidGlassBorderRed,
    isHighlighted: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val effectiveBorderColor = if (isHighlighted) NeonRedBright else borderColor

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isHighlighted) 20.dp else 14.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.65f),
                spotColor = if (isHighlighted) NeonRed else NeonRed.copy(alpha = 0.35f)
            )
            .clip(shape)
            // Liquid Glass Body: multi-stop frosted white gradient
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LiquidGlassBodyTop,
                        LiquidGlassBodyMid,
                        LiquidGlassBodyBottom
                    )
                )
            )
            // Top specular shine streak + diagonal refraction highlight
            .drawBehind {
                // Top specular highlight line
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            LiquidGlassSpecularTop,
                            LiquidGlassSpecularTop,
                            Color.Transparent
                        )
                    ),
                    start = Offset(16.dp.toPx(), 1.dp.toPx()),
                    end = Offset(size.width - 16.dp.toPx(), 1.dp.toPx()),
                    strokeWidth = 2.dp.toPx()
                )

                // Diagonal glossy reflection across the upper portion
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            LiquidGlassGlossShine.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.width * 0.6f, size.height * 0.45f)
                    ),
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height * 0.45f)
                )
            }
            // Glass Rim Border (top has white specular tint, sides/bottom have glowing red tint)
            .border(
                border = BorderStroke(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            LiquidGlassSpecularMid,
                            effectiveBorderColor.copy(alpha = 0.8f),
                            effectiveBorderColor.copy(alpha = 0.3f)
                        )
                    )
                ),
                shape = shape
            )
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

/**
 * Liquid Glass Action Button with glossy dome reflection and pulsating neon red core
 */
@Composable
fun GlassActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(16.dp)
    val buttonBrush = if (enabled) {
        Brush.verticalGradient(
            colors = listOf(
                NeonRedBright,
                NeonRed,
                NeonRedDim
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x33FFFFFF),
                Color(0x18FFFFFF)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 16.dp else 0.dp,
                shape = shape,
                spotColor = NeonRed,
                ambientColor = NeonRed.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(buttonBrush)
            // Liquid top gloss reflection
            .drawBehind {
                if (enabled) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x80FFFFFF),
                                Color(0x1AFFFFFF),
                                Color.Transparent
                            )
                        ),
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height * 0.48f)
                    )
                    // Edge specular rim
                    drawLine(
                        color = Color.White.copy(alpha = 0.85f),
                        start = Offset(12.dp.toPx(), 1.dp.toPx()),
                        end = Offset(size.width - 12.dp.toPx(), 1.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xEEFFFFFF),
                        NeonRedBright,
                        NeonRedDim.copy(alpha = 0.4f)
                    )
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) Color.White else TextGrayMuted,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.2.sp
            )
        }
    }
}

/**
 * Recessed glass well for coordinates and inputs.
 * Appears carved into the liquid glass sheet with inner shadow and glowing rim.
 */
@Composable
fun LiquidGlassWell(
    label: String,
    value: String,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    sublabel: String? = null,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val borderColor = if (isActive) NeonRedBright else LiquidGlassBorder.copy(alpha = 0.4f)
    val wellBg = if (isActive) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x55FF2A55),
                Color(0x33FF2A55),
                Color(0x2211111E)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                LiquidGlassRecessed,
                Color(0x33000000),
                Color(0x1AFFFFFF)
            )
        )
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isActive) 10.dp else 2.dp,
                shape = shape,
                spotColor = if (isActive) NeonRed else Color.Transparent
            )
            .clip(shape)
            .background(wellBg)
            // Top inner bevel edge
            .drawBehind {
                drawLine(
                    color = if (isActive) Color.White.copy(alpha = 0.7f) else Color(0x33FFFFFF),
                    start = Offset(6.dp.toPx(), 1.dp.toPx()),
                    end = Offset(size.width - 6.dp.toPx(), 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = NeonRedBright)
            )
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = if (isActive) NeonRedBright else LaserCyan.copy(alpha = 0.9f),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                )
                if (sublabel != null) {
                    Text(
                        text = sublabel,
                        color = TextGrayMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (value.isEmpty()) "0" else value,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}

/**
 * Status tag for Valid/Invalid state with glowing liquid bubble indicator
 */
@Composable
fun StatusBadge(
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (isValid) Color(0x4422C55E) else Color(0x55EF4444)
    val border = if (isValid) Color(0xFF22C55E) else Color(0xFFFF3B30)
    val text = if (isValid) "READY" else "ERROR"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
