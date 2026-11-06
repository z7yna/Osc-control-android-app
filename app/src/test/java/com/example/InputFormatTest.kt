package com.example

import com.example.ui.FireControlUiState
import com.example.ui.components.ActiveField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InputFormatTest {
    @Test
    fun defaultsAreValidFormat() {
        assertTrue(FireControlUiState().inputFormatErrors().isEmpty())
    }

    @Test
    fun lonelyMinusIsReported() {
        val errors = FireControlUiState(targetXStr = "-").inputFormatErrors()
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Target X"))
    }

    @Test
    fun trailingDotIsReported() {
        assertEquals(1, FireControlUiState(targetZStr = "10.").inputFormatErrors().size)
    }

    @Test
    fun decimalNotAllowedInPasscodeOrOrigin() {
        assertEquals(1, FireControlUiState(passcodeStr = "9.5").inputFormatErrors().size)
        assertEquals(1, FireControlUiState(originXStr = "16.5").inputFormatErrors().size)
    }

    @Test
    fun targetAcceptsNegativeDecimal() {
        assertTrue(FireControlUiState(targetXStr = "-12.5").inputFormatErrors().isEmpty())
    }

    @Test
    fun fieldLimitsAreSane() {
        assertEquals(8, ActiveField.TARGET_X.maxIntDigits)
        assertTrue(!ActiveField.ORIGIN_X.allowsDecimal)
    }
}
