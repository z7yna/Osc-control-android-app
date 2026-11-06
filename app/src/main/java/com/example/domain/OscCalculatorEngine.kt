package com.example.domain

import kotlin.math.*

object OscCalculatorEngine {

    // Constants from Excel / TypeScript formulas
    private const val DV_PLUS_X = 8.755553287151535
    private const val DV_PLUS_Y = 8.816216069168268
    private const val DV_PLUS_Z = 8.755553287151535
    private const val DV_MINUS_X = -8.664591055789826
    private const val DV_MINUS_Z = -8.664591055789826
    private const val DV_YX = -0.596968396919667
    private const val DY_MS = 3.51
    private const val DY_STAB = 106.92 + 44.4
    private const val DY_NUKE = 140.0
    private const val DX = 22.5000000095367
    private const val DZ = 0.9999999904633 - 1.0
    private const val DXZ_MS = 8.43750000953673
    private const val SLOWDOWN_XZ = 0.900000035762786
    private const val SLOWDOWN_Y = 1.500000000000000

    const val SFC_SCALE = 100.0 / 1233.0
    const val SFC_MAX_MAGNITUDE = 32767
    private const val SFC_POST_ACCEL_TICKS = 74

    private val SFC_PAYLOAD_BUILD_TICKS = mapOf(
        16 to 667,
        64 to 1074,
        256 to 3026,
        1024 to 10770
    )

    private val EMPTY_BINARY_GRID: List<List<Int>> = List(5) { List(13) { 0 } }

    fun overworldToNether(coords: Coordinates): Coordinates {
        return Coordinates(
            x = floor(coords.x / 8.0),
            y = coords.y,
            z = floor(coords.z / 8.0)
        )
    }

    fun netherToOverworld(coords: Coordinates): Coordinates {
        return Coordinates(
            x = coords.x * 8.0,
            y = coords.y,
            z = coords.z * 8.0
        )
    }

    fun calculate(
        origin: Coordinates,
        targetCoords: Coordinates,
        fireMode: FireMode = FireMode.NUKE,
        nukeSize: Int = 10,
        stabDepth: Int = 10,
        magazineSlot: Int = 0,
        passcode: Int = 940,
        cannonOrigin: CannonOriginVariant = CannonOriginVariant.OSC_MK6
    ): CalculationResult {
        val dx = targetCoords.x - origin.x
        val dz = targetCoords.z - origin.z
        val distance = sqrt(dx * dx + dz * dz)
        // Minecraft azimuth: 0 = North (-Z), 90 = East (+X), 180 = South (+Z), 270 = West (-X)
        val rad = atan2(dx, -dz)
        var deg = Math.toDegrees(rad)
        if (deg < 0) deg += 360.0

        if (cannonOrigin == CannonOriginVariant.OSC_SFC) {
            return calculateSfc(origin, targetCoords, nukeSize, distance, deg)
        }

        val useOscMs = cannonOrigin == CannonOriginVariant.OSC_MS

        val dvX = if (targetCoords.x > origin.x) DV_PLUS_X else DV_MINUS_X
        val dvZ = if (targetCoords.z > origin.z) DV_PLUS_Z else DV_MINUS_Z

        val baseDY = if (fireMode == FireMode.NUKE) DY_NUKE else DY_STAB
        val dy = if (useOscMs) baseDY - DY_MS else baseDY

        val dX = if (useOscMs) DXZ_MS else DX
        val dZ = if (useOscMs) DXZ_MS else DZ

        val iX = origin.x + dX + magazineSlot
        val iY = origin.y + 2.1975
        val iZ = origin.z + dZ

        var diffY = (dy + targetCoords.y) - iY
        if (diffY < 50.0) diffY = 50.0

        val countY = diffY / (SLOWDOWN_Y * DV_PLUS_Y)
        val coarseY = floor(countY).toInt()
        val fineY = round((countY - coarseY) * 10.0).toInt()

        val diffX = targetCoords.x - (iX + (coarseY + fineY / 10.0) * DV_YX)
        val diffZ = targetCoords.z - iZ

        val countX = diffX / (SLOWDOWN_XZ * dvX)
        val countZ = diffZ / (SLOWDOWN_XZ * dvZ)

        val coarseX = floor(abs(countX)).toInt()
        val fineX = round((abs(countX) - coarseX) * 10.0).toInt()

        val coarseZ = floor(abs(countZ)).toInt()
        val fineZ = round((abs(countZ) - coarseZ) * 10.0).toInt()

        val vX = dvX * (coarseX + fineX / 10.0) + DV_YX * (coarseY + fineY / 10.0)
        val vY = DV_PLUS_Y * (coarseY + fineY / 10.0)
        val vZ = dvZ * (coarseZ + fineZ / 10.0)

        val pX = iX + SLOWDOWN_XZ * vX
        val pY = iY + SLOWDOWN_Y * vY
        val pZ = iZ + SLOWDOWN_XZ * vZ

        val coarse = Vector3I(coarseX, coarseY, coarseZ)
        val fine = Vector3I(fineX, fineY, fineZ)
        val diff = Vector3D(diffX, diffY, diffZ)

        val isNuke = fireMode == FireMode.NUKE
        val payloadSize = if (isNuke) nukeSize else ceil(stabDepth / (9.0 * 0.99)).toInt()

        val binaryGrid = generateBinaryGrid(coarse, fine, passcode, diff, isNuke, payloadSize)
        val timeBreakdown = calculateTimeBreakdown(fireMode, nukeSize, stabDepth, coarseX, coarseY, coarseZ)

        val payloadTnt = if (isNuke) {
            (nukeSize * (nukeSize + 1)) / 2
        } else {
            ceil(stabDepth / (9.0 * 0.99)).toInt()
        }
        val payloadDesc = if (isNuke) {
            "Nuke Size $nukeSize ($payloadTnt warhead charges)"
        } else {
            "Stab Depth $stabDepth ($payloadTnt charges)"
        }
        val accelTnt = coarseX + coarseY + coarseZ
        val totalTnt = payloadTnt + accelTnt
        val stacksBreakdown = "${totalTnt / 64} stacks + ${totalTnt % 64} TNT"

        val tntReqs = TntRequirements(
            payloadTnt = payloadTnt,
            payloadDescription = payloadDesc,
            accelTnt = accelTnt,
            accelX = coarseX,
            accelY = coarseY,
            accelZ = coarseZ,
            totalTnt = totalTnt,
            stacksBreakdown = stacksBreakdown
        )

        return CalculationResult(
            diff = diff,
            dvPlus = Vector3D(DV_PLUS_X, DV_PLUS_Y, DV_PLUS_Z),
            dvMinus = Vector3D(DV_MINUS_X, DV_PLUS_Y, DV_MINUS_Z),
            count = Vector3D(countX, countY, countZ),
            coarse = coarse,
            fine = fine,
            binaryGrid = binaryGrid,
            exactPos = Coordinates(pX, pY, pZ),
            initialPosition = Coordinates(iX, iY, iZ),
            exactMotion = Vector3D(vX, vY, vZ),
            netherPos = overworldToNether(origin),
            timeEstimate = timeBreakdown.total,
            timeBreakdown = timeBreakdown,
            distance = distance,
            azimuthDegrees = deg,
            tntRequirements = tntReqs
        )
    }

    private fun calculateSfc(
        origin: Coordinates,
        target: Coordinates,
        payloadSize: Int,
        distance: Double,
        azimuth: Double
    ): CalculationResult {
        val dx = target.x - origin.x
        val dz = target.z - origin.z
        val orange = round(SFC_SCALE * (dx + dz)).toInt()
        val blue = round(SFC_SCALE * (dx - dz)).toInt()

        val sfcTerminal = SfcTerminal(
            orange = orange,
            blue = blue,
            orangeBits = to15BitsMsbLeft(orange),
            blueBits = to15BitsMsbLeft(blue)
        )

        val timeBreakdown = calculateSfcTimeBreakdown(payloadSize, orange, blue)
        val initialPosition = Coordinates(origin.x, origin.y + 20.5, origin.z)
        val recovered = sfcInverseDelta(orange, blue)
        val exactPos = Coordinates(origin.x + recovered.x, origin.y + 20.5, origin.z + recovered.z)

        val sfcAccelTnt = abs(orange) + abs(blue)
        val sfcTotalTnt = payloadSize + sfcAccelTnt
        val sfcTntReqs = TntRequirements(
            payloadTnt = payloadSize,
            payloadDescription = "SFC Fixed Payload ($payloadSize blocks)",
            accelTnt = sfcAccelTnt,
            accelX = abs(orange),
            accelY = 0,
            accelZ = abs(blue),
            totalTnt = sfcTotalTnt,
            stacksBreakdown = "${sfcTotalTnt / 64} stacks + ${sfcTotalTnt % 64} TNT"
        )

        return CalculationResult(
            diff = Vector3D(dx, target.y - origin.y, dz),
            dvPlus = Vector3D(0.0, 0.0, 0.0),
            dvMinus = Vector3D(0.0, 0.0, 0.0),
            count = Vector3D(orange.toDouble(), 0.0, blue.toDouble()),
            coarse = Vector3I(0, 0, 0),
            fine = Vector3I(0, 0, 0),
            binaryGrid = EMPTY_BINARY_GRID,
            sfcTerminal = sfcTerminal,
            exactPos = exactPos,
            initialPosition = initialPosition,
            exactMotion = Vector3D(recovered.x, 0.0, recovered.z),
            netherPos = overworldToNether(origin),
            timeEstimate = timeBreakdown.total,
            timeBreakdown = timeBreakdown,
            distance = distance,
            azimuthDegrees = azimuth,
            tntRequirements = sfcTntReqs
        )
    }

    private fun sfcInverseDelta(orange: Int, blue: Int): Vector3D {
        val inv = 1233.0 / 100.0
        return Vector3D(
            x = ((orange + blue) * inv) / 2.0,
            y = 0.0,
            z = ((orange - blue) * inv) / 2.0
        )
    }

    fun to15BitsMsbLeft(value: Int): List<Int> {
        val mag = min(abs(value), SFC_MAX_MAGNITUDE)
        return (14 downTo 0).map { i -> (mag shr i) and 1 }
    }

    private fun extractBit(value: Int, divisor: Int): Int {
        return (abs(value) / divisor) % 2
    }

    private fun extractInvertedBit(value: Int, divisor: Int): Int {
        return 1 - extractBit(value, divisor)
    }

    private fun generateBinaryGrid(
        coarse: Vector3I,
        fine: Vector3I,
        passcode: Int,
        diff: Vector3D,
        isNuke: Boolean,
        payloadSize: Int
    ): List<List<Int>> {
        val row0 = listOf(
            extractBit(coarse.z, 1),
            extractBit(coarse.z, 32),
            extractBit(coarse.z, 1024),
            extractInvertedBit(fine.z, 8),
            extractInvertedBit(fine.y, 8),
            extractBit(coarse.y, 8),
            extractInvertedBit(fine.x, 8),
            extractBit(coarse.x, 8192),
            extractBit(coarse.x, 256),
            extractBit(coarse.x, 8),
            extractBit(payloadSize, 2),
            extractBit(passcode, 512),
            extractBit(passcode, 16)
        )
        val row1 = listOf(
            extractBit(coarse.z, 2),
            extractBit(coarse.z, 64),
            extractBit(coarse.z, 2048),
            extractInvertedBit(fine.z, 4),
            extractInvertedBit(fine.y, 4),
            extractBit(coarse.y, 4),
            extractInvertedBit(fine.x, 4),
            extractBit(coarse.x, 4096),
            extractBit(coarse.x, 128),
            extractBit(coarse.x, 4),
            extractBit(payloadSize, 4),
            extractBit(passcode, 256),
            extractBit(passcode, 8)
        )
        val row2 = listOf(
            extractBit(coarse.z, 4),
            extractBit(coarse.z, 128),
            extractBit(coarse.z, 4096),
            extractInvertedBit(fine.z, 2),
            extractInvertedBit(fine.y, 2),
            extractBit(coarse.y, 2),
            extractInvertedBit(fine.x, 2),
            extractBit(coarse.x, 2048),
            extractBit(coarse.x, 64),
            extractBit(coarse.x, 2),
            extractBit(payloadSize, 8),
            extractBit(passcode, 128),
            extractBit(passcode, 4)
        )
        val row3 = listOf(
            extractBit(coarse.z, 8),
            extractBit(coarse.z, 256),
            extractBit(coarse.z, 8192),
            extractInvertedBit(fine.z, 1),
            extractInvertedBit(fine.y, 1),
            extractBit(coarse.y, 1),
            extractInvertedBit(fine.x, 1),
            extractBit(coarse.x, 1024),
            extractBit(coarse.x, 32),
            extractBit(coarse.x, 1),
            extractBit(payloadSize, 16),
            extractBit(passcode, 64),
            extractBit(passcode, 2)
        )
        val row4 = listOf(
            extractBit(coarse.z, 16),
            extractBit(coarse.z, 512),
            extractBit(coarse.z, 16384),
            if (diff.z < 0) 1 else 0,
            extractBit(coarse.y, 16),
            if (diff.x < 0) 1 else 0,
            extractBit(coarse.x, 16384),
            extractBit(coarse.x, 512),
            extractBit(coarse.x, 16),
            extractBit(payloadSize, 1),
            if (isNuke) 1 else 0,
            extractBit(passcode, 32),
            extractBit(passcode, 1)
        )
        return listOf(row0, row1, row2, row3, row4)
    }

    private fun formatTimeSeconds(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    private fun calculateTimeBreakdown(
        fireMode: FireMode,
        nukeSize: Int,
        stabDepth: Int,
        coarseX: Int,
        coarseY: Int,
        coarseZ: Int
    ): TimeBreakdown {
        val accelerationTicks = 6 * (max(coarseX, coarseZ) + coarseY + 2)
        val payloadTicks: Double = if (fireMode == FireMode.NUKE) {
            6.0 * ((nukeSize * (nukeSize + 1.0)) / 2.0 + nukeSize * 2.4375) + 930.0
        } else {
            6.0 * (2.0 * round(stabDepth / (9.0 * 0.99))) + 1300.0
        }

        val payloadSeconds = ceil(payloadTicks / 20.0).toInt()
        val accelerationSeconds = ceil(accelerationTicks / 20.0).toInt()
        val totalSeconds = ceil((payloadTicks + accelerationTicks) / 20.0).toInt()

        return TimeBreakdown(
            total = formatTimeSeconds(totalSeconds),
            payloadBuilding = formatTimeSeconds(payloadSeconds),
            acceleration = formatTimeSeconds(accelerationSeconds)
        )
    }

    private fun calculateSfcTimeBreakdown(
        payloadSize: Int,
        orange: Int,
        blue: Int
    ): TimeBreakdown {
        val payloadTicks = SFC_PAYLOAD_BUILD_TICKS[payloadSize] ?: SFC_PAYLOAD_BUILD_TICKS[16]!!
        val accelerationTicks = max(abs(orange), abs(blue))
        val totalTicks = payloadTicks + accelerationTicks + SFC_POST_ACCEL_TICKS

        return TimeBreakdown(
            total = formatTimeSeconds(ceil(totalTicks / 20.0).toInt()),
            payloadBuilding = formatTimeSeconds(ceil(payloadTicks / 20.0).toInt()),
            acceleration = formatTimeSeconds(ceil(accelerationTicks / 20.0).toInt())
        )
    }

    fun validate(
        origin: Coordinates,
        target: Coordinates,
        fireMode: FireMode = FireMode.NUKE,
        nukeSize: Int = 10,
        stabDepth: Int = 10,
        magazineSlot: Int = 0,
        passcode: Int = 940,
        cannonOrigin: CannonOriginVariant = CannonOriginVariant.OSC_MK6
    ): ValidationResult {
        val errors = mutableListOf<String>()
        val isSfc = cannonOrigin == CannonOriginVariant.OSC_SFC

        // Origin ÷16 alignment applies to OSC Mk6 / MS only (not SFC)
        if (!isSfc) {
            if (origin.x.toInt() % 16 != 0) {
                errors.add("Origin X (${origin.x.toInt()}) must be divisible by 16")
            }
            if (origin.y.toInt() % 16 != 0) {
                errors.add("Origin Y (${origin.y.toInt()}) must be divisible by 16")
            }
            if (origin.z.toInt() % 16 != 0) {
                errors.add("Origin Z (${origin.z.toInt()}) must be divisible by 16")
            }
        }

        if (isSfc) {
            if (!SFC_PAYLOAD_SIZES.contains(nukeSize)) {
                errors.add("Payload Size ($nukeSize) must be one of ${SFC_PAYLOAD_SIZES.joinToString(", ")}")
            }
        } else if (fireMode == FireMode.NUKE) {
            if (nukeSize < 1) errors.add("Nuke Size ($nukeSize) must be at least 1")
            if (nukeSize > 31) errors.add("Nuke Size ($nukeSize) must be 31 or less")
        } else {
            if (stabDepth < 1) errors.add("Stab Depth ($stabDepth) must be at least 1")
            val stabCheck = ceil(stabDepth / (9.0 * 0.99)).toInt()
            if (stabCheck > 31) errors.add("Stab Depth ($stabDepth) is too large (max ~275)")
        }

        // Distance check
        val dx = target.x - origin.x
        val dz = target.z - origin.z
        val distance = sqrt(dx * dx + dz * dz)
        if (distance < 64.0) {
            errors.add("Distance (${"%.1f".format(distance)}) must be at least 64 blocks")
        }

        // Passcode validation
        if (!isSfc && !ValidPasscodes.isValid(passcode)) {
            errors.add("Passcode ($passcode) is not in the valid passcode list")
        }

        // Calculation limits
        try {
            val result = calculate(
                origin = origin,
                targetCoords = target,
                fireMode = fireMode,
                nukeSize = nukeSize,
                stabDepth = stabDepth,
                magazineSlot = magazineSlot,
                passcode = passcode,
                cannonOrigin = cannonOrigin
            )

            if (isSfc && result.sfcTerminal != null) {
                if (abs(result.sfcTerminal.orange) > SFC_MAX_MAGNITUDE) {
                    errors.add("Orange channel (${result.sfcTerminal.orange}) exceeds 15-bit range")
                }
                if (abs(result.sfcTerminal.blue) > SFC_MAX_MAGNITUDE) {
                    errors.add("Blue channel (${result.sfcTerminal.blue}) exceeds 15-bit range")
                }
            } else {
                if (abs(result.count.x) >= 32767.999) {
                    errors.add("X distance is too large (count overflow)")
                }
                if (abs(result.count.z) >= 32767.999) {
                    errors.add("Z distance is too large (count overflow)")
                }
                if (abs(result.count.y) >= 31.999) {
                    errors.add("Y distance is too large (count overflow)")
                }
                if (abs(result.count.x) < 1.0 && abs(result.count.z) < 1.0) {
                    errors.add("Target is too close (both X and Z counts < 1)")
                }
            }
        } catch (e: Exception) {
            errors.add("Calculation error: ${e.message}")
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}
