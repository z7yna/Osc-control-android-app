package com.example

import com.example.domain.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testValidPasscodes() {
        assertTrue(ValidPasscodes.isValid(940))
        assertTrue(ValidPasscodes.isValid(63))
        assertTrue(ValidPasscodes.isValid(1023))
        assertFalse(ValidPasscodes.isValid(1234))
        assertFalse(ValidPasscodes.isValid(0))
    }

    @Test
    fun testOverworldToNether() {
        val overworld = Coordinates(800.0, 64.0, -1600.0)
        val nether = OscCalculatorEngine.overworldToNether(overworld)
        assertEquals(100.0, nether.x, 0.001)
        assertEquals(64.0, nether.y, 0.001)
        assertEquals(-200.0, nether.z, 0.001)
    }

    @Test
    fun testOscMk6Calculation() {
        val origin = Coordinates(0.0, 64.0, 0.0)
        val target = Coordinates(1024.0, 68.0, -2048.0)
        val result = OscCalculatorEngine.calculate(
            origin = origin,
            targetCoords = target,
            fireMode = FireMode.NUKE,
            nukeSize = 10,
            passcode = 940,
            cannonOrigin = CannonOriginVariant.OSC_MK6
        )

        assertNotNull(result)
        assertTrue(result.distance > 2000.0)
        assertTrue(result.coarse.x > 0)
        assertTrue(result.coarse.z > 0)
        assertEquals(5, result.binaryGrid.size)
        assertEquals(13, result.binaryGrid[0].size)
        assertEquals(55, result.tntRequirements.payloadTnt)
        assertTrue(result.tntRequirements.accelTnt > 0)
        assertEquals(
            result.tntRequirements.payloadTnt + result.tntRequirements.accelTnt,
            result.tntRequirements.totalTnt
        )
    }

    @Test
    fun testSfcCalculation() {
        val origin = Coordinates(0.0, 64.0, 0.0)
        val target = Coordinates(500.0, 64.0, 500.0)
        val result = OscCalculatorEngine.calculate(
            origin = origin,
            targetCoords = target,
            cannonOrigin = CannonOriginVariant.OSC_SFC
        )

        assertNotNull(result.sfcTerminal)
        assertEquals(15, result.sfcTerminal!!.orangeBits.size)
        assertEquals(15, result.sfcTerminal!!.blueBits.size)
    }

    @Test
    fun testValidation() {
        // Origin not divisible by 16 should fail for Mk6
        val invalidOrigin = Coordinates(5.0, 64.0, 0.0)
        val target = Coordinates(1024.0, 68.0, -2048.0)
        val validation = OscCalculatorEngine.validate(
            origin = invalidOrigin,
            target = target,
            cannonOrigin = CannonOriginVariant.OSC_MK6
        )
        assertFalse(validation.isValid)
        assertTrue(validation.errors.any { it.contains("divisible by 16") })

        // Target too close (< 64 blocks) should fail
        val closeTarget = Coordinates(10.0, 64.0, 10.0)
        val closeValidation = OscCalculatorEngine.validate(
            origin = Coordinates(0.0, 64.0, 0.0),
            target = closeTarget,
            cannonOrigin = CannonOriginVariant.OSC_MK6
        )
        assertFalse(closeValidation.isValid)
        assertTrue(closeValidation.errors.any { it.contains("must be at least 64") })
    }
}
