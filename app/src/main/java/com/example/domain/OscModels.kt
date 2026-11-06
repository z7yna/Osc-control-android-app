package com.example.domain

data class Coordinates(
    val x: Double = 0.0,
    val y: Double = 64.0,
    val z: Double = 0.0
) {
    fun toIntTriple(): Triple<Int, Int, Int> = Triple(x.toInt(), y.toInt(), z.toInt())
}

enum class CannonOriginVariant(val displayName: String, val id: String) {
    OSC_MK6("OSC Mk 6", "osc-mk6"),
    OSC_MS("OSC MS", "osc-ms"),
    OSC_SFC("OSC SFC", "osc-sfc")
}

enum class FireMode(val displayName: String, val id: String) {
    NUKE("Nuke Mode", "nuke"),
    STAB("Stab Mode", "stab")
}

val SFC_PAYLOAD_SIZES = listOf(16, 64, 256, 1024)

data class SfcTerminal(
    val orange: Int,
    val blue: Int,
    val orangeBits: List<Int>,
    val blueBits: List<Int>
)

data class TimeBreakdown(
    val total: String,
    val payloadBuilding: String,
    val acceleration: String
)

data class Vector3D(
    val x: Double,
    val y: Double,
    val z: Double
)

data class Vector3I(
    val x: Int,
    val y: Int,
    val z: Int
)

data class TntRequirements(
    val payloadTnt: Int,
    val payloadDescription: String,
    val accelTnt: Int,
    val accelX: Int,
    val accelY: Int,
    val accelZ: Int,
    val totalTnt: Int,
    val stacksBreakdown: String
)

data class CalculationResult(
    val diff: Vector3D,
    val dvPlus: Vector3D,
    val dvMinus: Vector3D,
    val count: Vector3D,
    val coarse: Vector3I,
    val fine: Vector3I,
    val binaryGrid: List<List<Int>>,
    val sfcTerminal: SfcTerminal? = null,
    val exactPos: Coordinates,
    val initialPosition: Coordinates,
    val exactMotion: Vector3D,
    val netherPos: Coordinates,
    val timeEstimate: String,
    val timeBreakdown: TimeBreakdown,
    val distance: Double,
    val azimuthDegrees: Double,
    val tntRequirements: TntRequirements
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)
