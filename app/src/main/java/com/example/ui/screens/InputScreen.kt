package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CannonOriginVariant
import com.example.domain.Coordinates
import com.example.domain.FireMode
import com.example.domain.SFC_PAYLOAD_SIZES
import com.example.ui.FireControlUiState
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun InputScreen(
    state: FireControlUiState,
    onSelectField: (ActiveField) -> Unit,
    onSetCannonVariant: (CannonOriginVariant) -> Unit,
    onSetFireMode: (FireMode) -> Unit,
    onSetOriginPreset: (Coordinates) -> Unit,
    onCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp, bottom = if (state.activeField != null) 270.dp else 45.dp)
    ) {
        // App Header
        AppHeader(
            selectedVariant = state.cannonVariant,
            onSelectVariant = onSetCannonVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Cannon Origin Card
        CannonOriginCard(
            state = state,
            onSelectField = onSelectField,
            onSetPreset = onSetOriginPreset
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Target Coordinates Card
        TargetCard(
            state = state,
            onSelectField = onSelectField,
            onSetFireMode = onSetFireMode
        )

        // Validation Errors Section
        if (!state.validationResult.isValid) {
            Spacer(modifier = Modifier.height(14.dp))
            ValidationErrorPanel(errors = state.validationResult.errors)
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Big Primary "Calc" Button
        GlassActionButton(
            text = "CALCULATE FIRING SOLUTION",
            onClick = onCalculate,
            enabled = state.validationResult.isValid,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        )
    }
}

@Composable
private fun AppHeader(
    selectedVariant: CannonOriginVariant,
    onSelectVariant: (CannonOriginVariant) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Glowing Radar Badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(12.dp, CircleShape, spotColor = NeonRed)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(NeonRedBright.copy(alpha = 0.5f), Color(0x33000000))
                        )
                    )
                    .border(1.5.dp, NeonRedBright, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "OSC FIRE CONTROL",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "ORBITAL STRIKE CANNON MK 6",
                    color = NeonRedBright,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Liquid Glass Variant Selector Segmented Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = Color.Black)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x33FFFFFF), Color(0x18FFFFFF))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.6f), Color(0x33FF2A55))),
                    RoundedCornerShape(14.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CannonOriginVariant.entries.forEach { variant ->
                val isSelected = selectedVariant == variant
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .shadow(if (isSelected) 8.dp else 0.dp, RoundedCornerShape(10.dp), spotColor = NeonRed)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (isSelected) {
                                Modifier.background(Brush.verticalGradient(listOf(NeonRedBright, NeonRed, NeonRedDim)))
                            } else {
                                Modifier.background(Color.Transparent)
                            }
                        )
                        .drawBehind {
                            if (isSelected) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.8f),
                                    start = Offset(8.dp.toPx(), 1.dp.toPx()),
                                    end = Offset(size.width - 8.dp.toPx(), 1.dp.toPx()),
                                    strokeWidth = 1.5.dp.toPx()
                                )
                            }
                        }
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectVariant(variant) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = variant.displayName,
                        color = if (isSelected) Color.White else TextGraySecondary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CannonOriginCard(
    state: FireControlUiState,
    onSelectField: (ActiveField) -> Unit,
    onSetPreset: (Coordinates) -> Unit
) {
    val isSfc = state.cannonVariant == CannonOriginVariant.OSC_SFC
    val isHighlighted = state.activeField in listOf(
        ActiveField.ORIGIN_X, ActiveField.ORIGIN_Y, ActiveField.ORIGIN_Z, ActiveField.PASSCODE
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        isHighlighted = isHighlighted
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CANNON ORIGIN",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )

            // Preset Origins: (0, 64, 0)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                PresetPill(label = "0, 64, 0") {
                    onSetPreset(Coordinates(0.0, 64.0, 0.0))
                }
                PresetPill(label = "Spawn") {
                    onSetPreset(Coordinates(0.0, 100.0, 0.0))
                }
            }
        }

        // Coordinates row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LiquidGlassWell(
                label = "ORIGIN X",
                value = state.originXStr,
                isActive = state.activeField == ActiveField.ORIGIN_X,
                modifier = Modifier.weight(1f),
                onClick = { onSelectField(ActiveField.ORIGIN_X) }
            )
            LiquidGlassWell(
                label = "ORIGIN Y",
                value = state.originYStr,
                isActive = state.activeField == ActiveField.ORIGIN_Y,
                modifier = Modifier.weight(1f),
                onClick = { onSelectField(ActiveField.ORIGIN_Y) }
            )
            LiquidGlassWell(
                label = "ORIGIN Z",
                value = state.originZStr,
                isActive = state.activeField == ActiveField.ORIGIN_Z,
                modifier = Modifier.weight(1f),
                onClick = { onSelectField(ActiveField.ORIGIN_Z) }
            )
        }

        if (!isSfc) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Requires coordinates ÷ 16",
                    color = LaserCyan.copy(alpha = 0.9f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )

                // Passcode button with liquid glass well styling
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (state.activeField == ActiveField.PASSCODE)
                                Color(0x55FF2A55)
                            else
                                Color(0x33000000)
                        )
                        .border(
                            1.dp,
                            if (state.activeField == ActiveField.PASSCODE) NeonRedBright else LiquidGlassBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectField(ActiveField.PASSCODE) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CODE: ",
                            color = TextGraySecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        Text(
                            text = state.passcodeStr,
                            color = NeonRedBright,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TargetCard(
    state: FireControlUiState,
    onSelectField: (ActiveField) -> Unit,
    onSetFireMode: (FireMode) -> Unit
) {
    val isSfc = state.cannonVariant == CannonOriginVariant.OSC_SFC
    val isHighlighted = state.activeField in listOf(
        ActiveField.TARGET_X, ActiveField.TARGET_Y, ActiveField.TARGET_Z,
        ActiveField.NUKE_SIZE, ActiveField.STAB_DEPTH, ActiveField.MAGAZINE_SLOT
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        isHighlighted = isHighlighted
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TARGET COORDINATES",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )

            // Fire Mode Toggle (Nuke vs Stab)
            if (!isSfc) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33000000))
                        .border(1.dp, LiquidGlassBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(3.dp)
                ) {
                    LiquidModePill(
                        label = "NUKE",
                        isSelected = state.fireMode == FireMode.NUKE,
                        onClick = { onSetFireMode(FireMode.NUKE) }
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    LiquidModePill(
                        label = "STAB",
                        isSelected = state.fireMode == FireMode.STAB,
                        onClick = { onSetFireMode(FireMode.STAB) }
                    )
                }
            }
        }

        // Coordinates row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LiquidGlassWell(
                label = "TARGET X",
                value = state.targetXStr,
                isActive = state.activeField == ActiveField.TARGET_X,
                modifier = Modifier.weight(1f),
                onClick = { onSelectField(ActiveField.TARGET_X) }
            )
            LiquidGlassWell(
                label = "TARGET Y",
                value = state.targetYStr,
                isActive = state.activeField == ActiveField.TARGET_Y,
                modifier = Modifier.weight(1f),
                onClick = { onSelectField(ActiveField.TARGET_Y) }
            )
            LiquidGlassWell(
                label = "TARGET Z",
                value = state.targetZStr,
                isActive = state.activeField == ActiveField.TARGET_Z,
                modifier = Modifier.weight(1f),
                onClick = { onSelectField(ActiveField.TARGET_Z) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Secondary controls: Payload Size & Magazine Slot
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isSfc) {
                // SFC Payload Size quick pills: 16, 64, 256, 1024
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PAYLOAD SIZE (BLOCKS)",
                        color = LaserCyan.copy(alpha = 0.9f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        SFC_PAYLOAD_SIZES.forEach { size ->
                            val isSelected = state.nukeSize == size
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .shadow(if (isSelected) 6.dp else 0.dp, RoundedCornerShape(8.dp), spotColor = NeonRed)
                                    .clip(RoundedCornerShape(8.dp))
                                    .then(
                                        if (isSelected) {
                                            Modifier.background(Brush.verticalGradient(listOf(NeonRedBright, NeonRed)))
                                        } else {
                                            Modifier.background(Color(0x33000000))
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.White.copy(alpha = 0.8f) else LiquidGlassBorder.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onSelectField(ActiveField.NUKE_SIZE) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = size.toString(),
                                    color = if (isSelected) Color.White else TextGraySecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                // Nuke Size / Stab Depth
                val isNuke = state.fireMode == FireMode.NUKE
                val label = if (isNuke) "NUKE SIZE" else "STAB DEPTH"
                val nukeTntCount = (state.nukeSize * (state.nukeSize + 1)) / 2
                val sublabel = if (isNuke) "$nukeTntCount TNT" else "BLOCKS"
                val value = if (isNuke) state.nukeSizeStr else state.stabDepthStr
                val field = if (isNuke) ActiveField.NUKE_SIZE else ActiveField.STAB_DEPTH

                LiquidGlassWell(
                    label = label,
                    sublabel = sublabel,
                    value = value,
                    isActive = state.activeField == field,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectField(field) }
                )

                LiquidGlassWell(
                    label = "MAGAZINE SLOT",
                    sublabel = "0 - 15",
                    value = state.magazineSlotStr,
                    isActive = state.activeField == ActiveField.MAGAZINE_SLOT,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectField(ActiveField.MAGAZINE_SLOT) }
                )
            }
        }
    }
}

@Composable
private fun PresetPill(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x33000000))
            .border(1.dp, LiquidGlassBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = LaserCyan,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LiquidModePill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .shadow(if (isSelected) 6.dp else 0.dp, RoundedCornerShape(8.dp), spotColor = NeonRed)
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (isSelected) {
                    Modifier.background(Brush.verticalGradient(listOf(NeonRedBright, NeonRed)))
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .border(
                1.dp,
                if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else TextGraySecondary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun ValidationErrorPanel(errors: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFFEF4444))
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x55440008))
            .border(1.2.dp, Color(0xFFFF3B30), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF3B30),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VALIDATION WARNINGS",
                    color = Color(0xFFFF3B30),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            errors.forEach { err ->
                Text(
                    text = "• $err",
                    color = Color(0xFFFED7D7),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = 1.5.dp)
                )
            }
        }
    }
}
