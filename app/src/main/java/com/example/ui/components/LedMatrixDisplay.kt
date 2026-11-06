package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CannonOriginVariant
import com.example.domain.SfcTerminal
import com.example.ui.theme.*

@Composable
fun LedMatrixDisplay(
    binaryGrid: List<List<Int>>,
    sfcTerminal: SfcTerminal?,
    cannonVariant: CannonOriginVariant,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = if (isValid) GlassBorderRed else Color(0x66EF4444)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (cannonVariant == CannonOriginVariant.OSC_SFC) "SFC DUAL-CHANNEL TERMINAL" else "BINARY PUNCH-CARD MATRIX (5x13)",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )
            StatusBadge(isValid = isValid)
        }

        if (cannonVariant == CannonOriginVariant.OSC_SFC && sfcTerminal != null) {
            SfcTerminalDisplay(sfcTerminal = sfcTerminal)
        } else {
            Mk6GridDisplay(binaryGrid = binaryGrid)
        }
    }
}

@Composable
private fun Mk6GridDisplay(binaryGrid: List<List<Int>>) {
    val columnColors = listOf(
        ColBlueDark, ColBlueDark, ColBlueDark,
        ColBlueLight,
        ColGreen, ColGreen,
        ColYellow,
        ColOrange, ColOrange, ColOrange,
        ColRed,
        ColBrown, ColBrown
    )

    val rowColors = listOf(Row1, Row2, Row3, Row4, Row5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF090A10))
            .border(1.dp, Color(0xFF23253A), RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header Colors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Corner
            Box(modifier = Modifier.size(10.dp).background(rowColors[0], RoundedCornerShape(2.dp)))
            columnColors.forEach { colColor ->
                Box(modifier = Modifier.size(12.dp, 6.dp).background(colColor, RoundedCornerShape(2.dp)))
            }
            // Corner
            Box(modifier = Modifier.size(10.dp).background(rowColors[0], RoundedCornerShape(2.dp)))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Matrix Rows
        binaryGrid.forEachIndexed { rowIdx, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Row Marker
                Box(
                    modifier = Modifier
                        .size(10.dp, 16.dp)
                        .background(rowColors.getOrElse(rowIdx) { Row1 }, RoundedCornerShape(2.dp))
                )

                // 13 LED Bulbs
                row.forEachIndexed { colIdx, value ->
                    LedBulb(
                        isOn = value == 1,
                        activeColor = LedRedOn,
                        inactiveColor = LedRedOff
                    )
                }

                // Right Row Marker
                Box(
                    modifier = Modifier
                        .size(10.dp, 16.dp)
                        .background(rowColors.getOrElse(rowIdx) { Row1 }, RoundedCornerShape(2.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bottom Header Colors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).background(rowColors[4], RoundedCornerShape(2.dp)))
            columnColors.forEach { colColor ->
                Box(modifier = Modifier.size(12.dp, 6.dp).background(colColor, RoundedCornerShape(2.dp)))
            }
            Box(modifier = Modifier.size(10.dp).background(rowColors[4], RoundedCornerShape(2.dp)))
        }
    }
}

@Composable
private fun SfcTerminalDisplay(sfcTerminal: SfcTerminal) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF090A10))
            .border(1.dp, Color(0xFF23253A), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        // Orange Channel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ORANGE: ${sfcTerminal.orange}",
                color = LedOrangeOn,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Text(
                text = if (sfcTerminal.orange < 0) "NEG (-)" else "POS (+)",
                color = if (sfcTerminal.orange < 0) NeonRedBright else LaserCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            sfcTerminal.orangeBits.forEach { bit ->
                LedBulb(
                    isOn = bit == 1,
                    activeColor = LedOrangeOn,
                    inactiveColor = Color(0xFF241508)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Blue Channel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BLUE: ${sfcTerminal.blue}",
                color = LedBlueOn,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Text(
                text = if (sfcTerminal.blue < 0) "NEG (-)" else "POS (+)",
                color = if (sfcTerminal.blue < 0) NeonRedBright else LaserCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            sfcTerminal.blueBits.forEach { bit ->
                LedBulb(
                    isOn = bit == 1,
                    activeColor = LedBlueOn,
                    inactiveColor = Color(0xFF0C1929)
                )
            }
        }
    }
}

@Composable
private fun LedBulb(
    isOn: Boolean,
    activeColor: Color,
    inactiveColor: Color
) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .shadow(
                elevation = if (isOn) 6.dp else 0.dp,
                shape = CircleShape,
                spotColor = activeColor,
                ambientColor = activeColor
            )
            .clip(CircleShape)
            .background(if (isOn) activeColor else inactiveColor)
            .border(
                width = 1.dp,
                color = if (isOn) Color.White.copy(alpha = 0.8f) else Color(0x33FFFFFF),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isOn) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}
