package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class ActiveField(
    val label: String,
    val allowsNegative: Boolean,
    val allowsDecimal: Boolean,
    val maxIntDigits: Int
) {
    ORIGIN_X("Origin X", true, false, 8),
    ORIGIN_Y("Origin Y", true, false, 8),
    ORIGIN_Z("Origin Z", true, false, 8),
    TARGET_X("Target X", true, true, 8),
    TARGET_Y("Target Y", true, true, 8),
    TARGET_Z("Target Z", true, true, 8),
    PASSCODE("Passcode", false, false, 4),
    NUKE_SIZE("Nuke Size", false, false, 4),
    STAB_DEPTH("Stab Depth", false, false, 3),
    MAGAZINE_SLOT("Magazine Slot", false, false, 2)
}

@Composable
fun CustomKeypad(
    activeField: ActiveField?,
    currentValue: String,
    onDigitPress: (String) -> Unit,
    onToggleSign: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onPreviousField: () -> Unit,
    onNextField: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    AnimatedVisibility(
        visible = activeField != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        if (activeField == null) return@AnimatedVisibility

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 28.dp,
                    shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
                    spotColor = NeonRed.copy(alpha = 0.6f)
                )
                .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                // Liquid Frosted Glass Tray
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xEE1E2138),
                            Color(0xF5101222),
                            Color(0xFA090A12)
                        )
                    )
                )
                // Specular rim on top
                .drawBehind {
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.9f),
                                Color.White.copy(alpha = 0.9f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(24.dp.toPx(), 1.dp.toPx()),
                        end = Offset(size.width - 24.dp.toPx(), 1.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                }
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            NeonRedBright,
                            Color(0x33FF2A55)
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
                )
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header Bar with active field name and current value preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EDITING: ${activeField.label.uppercase()}",
                            color = NeonRedBright,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (currentValue.isEmpty()) "0" else currentValue,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Previous / Next Nav
                        LiquidKeySmall(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onPreviousField()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Previous Field",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        LiquidKeySmall(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNextField()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Next Field",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Done / Collapse Button
                        Box(
                            modifier = Modifier
                                .height(38.dp)
                                .shadow(8.dp, RoundedCornerShape(10.dp), spotColor = NeonRed)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            NeonRedBright,
                                            NeonRed
                                        )
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onDismiss()
                                }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "DONE",
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Liquid Keypad Grid: 4 rows x 4 columns
                val rowHeight = 46.dp
                val spacing = 7.dp

                // Row 1: 1, 2, 3, +/-
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    LiquidNumKey("1", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("1")
                    }
                    LiquidNumKey("2", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("2")
                    }
                    LiquidNumKey("3", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("3")
                    }
                    LiquidNumKey(
                        text = "+ / -",
                        modifier = Modifier.weight(1f).height(rowHeight),
                        isAction = true,
                        enabled = activeField.allowsNegative
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleSign()
                    }
                }

                Spacer(modifier = Modifier.height(spacing))

                // Row 2: 4, 5, 6, ⌫
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    LiquidNumKey("4", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("4")
                    }
                    LiquidNumKey("5", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("5")
                    }
                    LiquidNumKey("6", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("6")
                    }
                    LiquidIconKey(
                        icon = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        modifier = Modifier.weight(1f).height(rowHeight),
                        isAction = true
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBackspace()
                    }
                }

                Spacer(modifier = Modifier.height(spacing))

                // Row 3: 7, 8, 9, CLR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    LiquidNumKey("7", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("7")
                    }
                    LiquidNumKey("8", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("8")
                    }
                    LiquidNumKey("9", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("9")
                    }
                    LiquidNumKey(
                        text = "CLR",
                        modifier = Modifier.weight(1f).height(rowHeight),
                        isAction = true
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClear()
                    }
                }

                Spacer(modifier = Modifier.height(spacing))

                // Row 4: ., 0, 00, ±16
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    LiquidNumKey(
                        text = ".",
                        modifier = Modifier.weight(1f).height(rowHeight),
                        enabled = activeField.allowsDecimal
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress(".")
                    }
                    LiquidNumKey("0", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("0")
                    }
                    LiquidNumKey("00", Modifier.weight(1f).height(rowHeight)) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPress("00")
                    }
                    LiquidNumKey(
                        text = "±16",
                        modifier = Modifier.weight(1f).height(rowHeight),
                        isAction = true
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        val num = currentValue.toIntOrNull() ?: 0
                        val rounded = if (num % 16 == 0) num + 16 else Math.round(num / 16.0f) * 16
                        onClear()
                        onDigitPress(rounded.toString())
                    }
                }
            }
        }
    }
}

@Composable
private fun LiquidNumKey(
    text: String,
    modifier: Modifier = Modifier,
    isAction: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val keyBg = when {
        !enabled -> Brush.verticalGradient(listOf(Color(0x11FFFFFF), Color(0x08FFFFFF)))
        isAction -> Brush.verticalGradient(listOf(Color(0x55FF2A55), Color(0x33FF2A55)))
        else -> Brush.verticalGradient(listOf(Color(0x40FFFFFF), Color(0x20FFFFFF)))
    }
    val textColor = when {
        !enabled -> TextGrayMuted
        isAction -> NeonRedBright
        else -> Color.White
    }
    val borderColor = when {
        !enabled -> Color(0x11FFFFFF)
        isAction -> NeonRed.copy(alpha = 0.8f)
        else -> Color.White.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .shadow(4.dp, shape, spotColor = if (isAction) NeonRed else Color.Black)
            .clip(shape)
            .background(keyBg)
            // Liquid top gloss reflection
            .drawBehind {
                if (enabled) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0x66FFFFFF), Color.Transparent)
                        ),
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height * 0.45f)
                    )
                }
            }
            .border(1.dp, borderColor, shape)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun LiquidIconKey(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    isAction: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val keyBg = if (isAction) {
        Brush.verticalGradient(listOf(Color(0x55FF2A55), Color(0x33FF2A55)))
    } else {
        Brush.verticalGradient(listOf(Color(0x40FFFFFF), Color(0x20FFFFFF)))
    }

    Box(
        modifier = modifier
            .shadow(4.dp, shape, spotColor = NeonRed)
            .clip(shape)
            .background(keyBg)
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0x66FFFFFF), Color.Transparent)
                    ),
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height * 0.45f)
                )
            }
            .border(1.dp, NeonRed.copy(alpha = 0.8f), shape)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = NeonRedBright)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = NeonRedBright,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun LiquidKeySmall(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.verticalGradient(listOf(Color(0x44FFFFFF), Color(0x20FFFFFF))))
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
