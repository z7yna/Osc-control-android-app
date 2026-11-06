package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CannonOriginVariant
import com.example.domain.FireMode
import com.example.ui.FireControlUiState
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ResultsScreen(
    state: FireControlUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val result = state.calculationResult ?: return

    BackHandler {
        onBack()
    }

    val copySolutionToClipboard = {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val isSfc = state.cannonVariant == CannonOriginVariant.OSC_SFC
        val text = if (isSfc && result.sfcTerminal != null) {
            """
            Target: ${state.targetName}
            Coordinates: X:${state.target.x.toInt()} Y:${state.target.y.toInt()} Z:${state.target.z.toInt()}
            Cannon: OSC SFC
            Payload Size: ${state.nukeSize}
            Orange Channel: ${result.sfcTerminal.orange}
            Blue Channel: ${result.sfcTerminal.blue}
            Distance: ${"%.1f".format(result.distance)} blocks
            Azimuth: ${"%.1f".format(result.azimuthDegrees)}°
            Total Time: ${result.timeEstimate}
            -------------------------
            Payload TNT: ${result.tntRequirements.payloadTnt} (${result.tntRequirements.payloadDescription})
            Acceleration TNT: ${result.tntRequirements.accelTnt} (X:${result.tntRequirements.accelX} Y:${result.tntRequirements.accelY} Z:${result.tntRequirements.accelZ})
            Total Mission TNT: ${result.tntRequirements.totalTnt} (${result.tntRequirements.stacksBreakdown})
            """.trimIndent()
        } else {
            val sizeOrDepth = if (state.fireMode == FireMode.NUKE)
                "Nuke Size: ${state.nukeSize}"
            else
                "Stab Depth: ${state.stabDepth}"

            """
            Target: ${state.targetName}
            Coordinates: X:${state.target.x.toInt()} Y:${state.target.y.toInt()} Z:${state.target.z.toInt()}
            Cannon: ${state.cannonVariant.displayName}
            Fire Mode: ${state.fireMode.displayName}
            $sizeOrDepth
            Magazine Slot: ${state.magazineSlot}
            Coarse: X:${result.coarse.x} Y:${result.coarse.y} Z:${result.coarse.z}
            Fine: X:${result.fine.x} Y:${result.fine.y} Z:${result.fine.z}
            Passcode: ${state.passcode}
            Nether Coords: X:${result.netherPos.x.toInt()} Y:${result.netherPos.y.toInt()} Z:${result.netherPos.z.toInt()}
            Distance: ${"%.1f".format(result.distance)} blocks
            Azimuth: ${"%.1f".format(result.azimuthDegrees)}°
            Total Time: ${result.timeEstimate}
            -------------------------
            Payload TNT: ${result.tntRequirements.payloadTnt} (${result.tntRequirements.payloadDescription})
            Acceleration TNT: ${result.tntRequirements.accelTnt} (X:${result.tntRequirements.accelX} Y:${result.tntRequirements.accelY} Z:${result.tntRequirements.accelZ})
            Total Mission TNT: ${result.tntRequirements.totalTnt} (${result.tntRequirements.stacksBreakdown})
            """.trimIndent()
        }

        val clip = ClipData.newPlainText("OSC Firing Solution", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Firing solution copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp, bottom = 45.dp)
    ) {
        // Results Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Liquid Back Button
            Row(
                modifier = Modifier
                    .shadow(6.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(listOf(Color(0x44FFFFFF), Color(0x20FFFFFF)))
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return to Inputs",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "INPUTS",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            // Liquid Copy Button
            Row(
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = NeonRed)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(listOf(Color(0x66FF2A55), Color(0x33FF2A55)))
                    )
                    .border(1.2.dp, NeonRedBright, RoundedCornerShape(12.dp))
                    .clickable { copySolutionToClipboard() }
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Solution",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "COPY SOLUTION",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Illustration: Cannon Alignment & Azimuth Schematic Canvas
        OscSchematicCanvas(
            origin = state.origin,
            target = state.target,
            result = result
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dedicated TNT Munitions Requirements Card (Payload + Acceleration)
        TntMunitionsCard(
            result = result,
            cannonVariant = state.cannonVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Primary Firing Solution Card
        if (state.cannonVariant != CannonOriginVariant.OSC_SFC) {
            FiringParametersCard(result = result)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Punch Card LED Matrix
        LedMatrixDisplay(
            binaryGrid = result.binaryGrid,
            sfcTerminal = result.sfcTerminal,
            cannonVariant = state.cannonVariant,
            isValid = state.validationResult.isValid
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Flight Timing Breakdown Card
        FlightTimingCard(result = result)

        Spacer(modifier = Modifier.height(16.dp))

        // Nether & Telemetry Card
        TelemetryCard(
            state = state,
            result = result
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Return Button
        GlassActionButton(
            text = "NEW CALCULATION",
            onClick = onBack,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        )
    }
}

@Composable
private fun FiringParametersCard(result: com.example.domain.CalculationResult) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "MECHANICAL ADJUSTMENTS",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Coarse Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x55000000))
                .border(1.dp, LaserCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COARSE",
                color = LaserCyan,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AxisValue("X", result.coarse.x.toString())
                AxisValue("Y", result.coarse.y.toString())
                AxisValue("Z", result.coarse.z.toString())
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fine Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x55000000))
                .border(1.dp, AccentOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FINE",
                color = AccentOrange,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AxisValue("X", result.fine.x.toString())
                AxisValue("Y", result.fine.y.toString())
                AxisValue("Z", result.fine.z.toString())
            }
        }
    }
}

@Composable
private fun AxisValue(axis: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$axis: ",
            color = TextGrayMuted,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun FlightTimingCard(result: com.example.domain.CalculationResult) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "MISSION TIMING",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeMetricBox(
                label = "TOTAL TIME",
                time = result.timeEstimate,
                color = NeonRedBright,
                modifier = Modifier.weight(1f)
            )
            TimeMetricBox(
                label = "PAYLOAD BUILD",
                time = result.timeBreakdown.payloadBuilding,
                color = LaserCyan,
                modifier = Modifier.weight(1f)
            )
            TimeMetricBox(
                label = "ACCELERATION",
                time = result.timeBreakdown.acceleration,
                color = AccentOrange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TimeMetricBox(
    label: String,
    time: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x55000000))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = TextGrayMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = time,
                color = color,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun TelemetryCard(
    state: FireControlUiState,
    result: com.example.domain.CalculationResult
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "TELEMETRY & NETHER LINK",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "NETHER CANNON COORDS",
                    color = TextGrayMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "X: ${result.netherPos.x.toInt()}  Y: ${result.netherPos.y.toInt()}  Z: ${result.netherPos.z.toInt()}",
                    color = LaserCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "TARGET DISTANCE",
                    color = TextGrayMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${"%,.1f".format(result.distance)} blocks",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun TntMunitionsCard(
    result: com.example.domain.CalculationResult,
    cannonVariant: CannonOriginVariant
) {
    val tnt = result.tntRequirements
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        isHighlighted = true
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = NeonRedBright,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TNT MUNITIONS REQUIRED",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x33FF2A55))
                    .border(1.dp, NeonRedBright, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${"%,d".format(tnt.totalTnt)} TNT",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // Row 1: Payload TNT & Acceleration Propellant TNT
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Payload TNT Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x55000000))
                    .border(1.dp, LaserCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "PAYLOAD TNT",
                        color = LaserCyan,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${"%,d".format(tnt.payloadTnt)}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tnt.payloadDescription,
                        color = TextGrayMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        maxLines = 1
                    )
                }
            }

            // Acceleration Propellant TNT Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x55000000))
                    .border(1.dp, NeonRedBright.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "PROPEL / ACCEL TNT",
                        color = NeonRedBright,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${"%,d".format(tnt.accelTnt)}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (cannonVariant == CannonOriginVariant.OSC_SFC)
                            "O: ${tnt.accelX} | B: ${tnt.accelZ} charges"
                        else
                            "X:${tnt.accelX} Y:${tnt.accelY} Z:${tnt.accelZ}",
                        color = TextGrayMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Total Stacks Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x33000000))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MINECRAFT INVENTORY STACKS",
                color = TextGrayMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
            )
            Text(
                text = tnt.stacksBreakdown,
                color = AccentOrange,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}
