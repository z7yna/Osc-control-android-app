package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PreferencesManager
import com.example.domain.*
import com.example.ui.components.ActiveField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ScreenMode {
    INPUT,
    RESULTS
}

data class FireControlUiState(
    val screenMode: ScreenMode = ScreenMode.INPUT,
    val activeField: ActiveField? = null,
    val cannonVariant: CannonOriginVariant = CannonOriginVariant.OSC_MK6,
    val fireMode: FireMode = FireMode.NUKE,
    val targetName: String = "Target 1",
    // String inputs to allow smooth editing without jumping
    val originXStr: String = "0",
    val originYStr: String = "64",
    val originZStr: String = "0",
    val targetXStr: String = "1024",
    val targetYStr: String = "68",
    val targetZStr: String = "-2048",
    val passcodeStr: String = "940",
    val nukeSizeStr: String = "10",
    val stabDepthStr: String = "10",
    val magazineSlotStr: String = "0",
    // Computed values
    val calculationResult: CalculationResult? = null,
    val validationResult: ValidationResult = ValidationResult(true, emptyList()),
    val errorMessage: String? = null
) {
    val origin: Coordinates
        get() = Coordinates(
            x = originXStr.toDoubleOrNull() ?: 0.0,
            y = originYStr.toDoubleOrNull() ?: 64.0,
            z = originZStr.toDoubleOrNull() ?: 0.0
        )

    val target: Coordinates
        get() = Coordinates(
            x = targetXStr.toDoubleOrNull() ?: 0.0,
            y = targetYStr.toDoubleOrNull() ?: 68.0,
            z = targetZStr.toDoubleOrNull() ?: 0.0
        )

    val passcode: Int
        get() = passcodeStr.toIntOrNull() ?: 940

    val nukeSize: Int
        get() = nukeSizeStr.toIntOrNull() ?: 10

    val stabDepth: Int
        get() = stabDepthStr.toIntOrNull() ?: 10

    val magazineSlot: Int
        get() = magazineSlotStr.toIntOrNull() ?: 0

    /** Strict format check so a half-typed value like "-" or "10." is reported, never silently replaced by a default. */
    fun inputFormatErrors(): List<String> {
        val intRegex = Regex("^-?\\d+$")
        val decimalRegex = Regex("^-?\\d+(\\.\\d+)?$")
        val errors = mutableListOf<String>()
        for (field in ActiveField.entries) {
            val value = getFieldValue(field)
            val regex = if (field.allowsDecimal) decimalRegex else intRegex
            val ok = regex.matches(value) && (field.allowsNegative || !value.startsWith("-"))
            if (!ok) {
                errors.add("${field.label} (\"$value\") is not a valid number")
            }
        }
        return errors
    }

    fun getFieldValue(field: ActiveField): String {
        return when (field) {
            ActiveField.ORIGIN_X -> originXStr
            ActiveField.ORIGIN_Y -> originYStr
            ActiveField.ORIGIN_Z -> originZStr
            ActiveField.TARGET_X -> targetXStr
            ActiveField.TARGET_Y -> targetYStr
            ActiveField.TARGET_Z -> targetZStr
            ActiveField.PASSCODE -> passcodeStr
            ActiveField.NUKE_SIZE -> nukeSizeStr
            ActiveField.STAB_DEPTH -> stabDepthStr
            ActiveField.MAGAZINE_SLOT -> magazineSlotStr
        }
    }
}

class FireControlViewModel(application: Application) : AndroidViewModel(application) {
    private companion object {
        const val MAX_FRACTION_DIGITS = 3
    }

    private val prefs = PreferencesManager(application)

    private val _uiState = MutableStateFlow(
        FireControlUiState(
            cannonVariant = prefs.cannonVariant,
            fireMode = prefs.fireMode,
            targetName = prefs.targetName,
            originXStr = prefs.originX,
            originYStr = prefs.originY,
            originZStr = prefs.originZ,
            targetXStr = prefs.targetX,
            targetYStr = prefs.targetY,
            targetZStr = prefs.targetZ,
            passcodeStr = prefs.passcode.toString(),
            nukeSizeStr = prefs.nukeSize.toString(),
            stabDepthStr = prefs.stabDepth.toString(),
            magazineSlotStr = prefs.magazineSlot.toString()
        )
    )
    val uiState: StateFlow<FireControlUiState> = _uiState.asStateFlow()

    init {
        recomputeValidation()
    }

    fun selectField(field: ActiveField?) {
        _uiState.update { it.copy(activeField = field) }
    }

    fun dismissKeypad() {
        _uiState.update { it.copy(activeField = null) }
    }

    fun nextField() {
        val current = _uiState.value.activeField ?: return
        val fields = ActiveField.entries
        val nextIndex = (fields.indexOf(current) + 1) % fields.size
        _uiState.update { it.copy(activeField = fields[nextIndex]) }
    }

    fun previousField() {
        val current = _uiState.value.activeField ?: return
        val fields = ActiveField.entries
        val prevIndex = if (fields.indexOf(current) - 1 < 0) fields.size - 1 else fields.indexOf(current) - 1
        _uiState.update { it.copy(activeField = fields[prevIndex]) }
    }

    fun onDigitPress(digit: String) {
        val currentField = _uiState.value.activeField ?: return
        val currentVal = _uiState.value.getFieldValue(currentField)

        if (digit == ".") {
            if (!currentField.allowsDecimal || currentVal.contains(".")) return
            updateFieldValue(currentField, currentVal + ".")
            return
        }

        val unsigned = currentVal.removePrefix("-")
        val intPart = unsigned.substringBefore(".")
        val fracPart = if (unsigned.contains(".")) unsigned.substringAfter(".") else null

        if (fracPart != null) {
            if (fracPart.length >= MAX_FRACTION_DIGITS) return
        } else {
            val replacingZero = intPart == "0"
            if (!replacingZero && intPart.length >= currentField.maxIntDigits) return
        }

        val newVal = if (fracPart == null && unsigned == "0") {
            if (currentVal.startsWith("-")) "-$digit" else digit
        } else {
            currentVal + digit
        }

        updateFieldValue(currentField, newVal)
    }

    fun onToggleSign() {
        val currentField = _uiState.value.activeField ?: return
        if (!currentField.allowsNegative) return

        val currentVal = _uiState.value.getFieldValue(currentField)
        if (currentVal == "0") return
        val newVal = if (currentVal.startsWith("-")) {
            currentVal.removePrefix("-")
        } else {
            "-$currentVal"
        }

        updateFieldValue(currentField, newVal)
    }

    fun onBackspace() {
        val currentField = _uiState.value.activeField ?: return
        val currentVal = _uiState.value.getFieldValue(currentField)

        val newVal = if (currentVal.length <= 1 || (currentVal.length == 2 && currentVal.startsWith("-"))) {
            "0"
        } else {
            currentVal.dropLast(1)
        }

        updateFieldValue(currentField, newVal)
    }

    fun onClear() {
        val currentField = _uiState.value.activeField ?: return
        updateFieldValue(currentField, "0")
    }

    private fun updateFieldValue(field: ActiveField, value: String) {
        _uiState.update { state ->
            when (field) {
                ActiveField.ORIGIN_X -> state.copy(originXStr = value)
                ActiveField.ORIGIN_Y -> state.copy(originYStr = value)
                ActiveField.ORIGIN_Z -> state.copy(originZStr = value)
                ActiveField.TARGET_X -> state.copy(targetXStr = value)
                ActiveField.TARGET_Y -> state.copy(targetYStr = value)
                ActiveField.TARGET_Z -> state.copy(targetZStr = value)
                ActiveField.PASSCODE -> state.copy(passcodeStr = value)
                ActiveField.NUKE_SIZE -> state.copy(nukeSizeStr = value)
                ActiveField.STAB_DEPTH -> state.copy(stabDepthStr = value)
                ActiveField.MAGAZINE_SLOT -> state.copy(magazineSlotStr = value)
            }
        }
        recomputeValidation()
    }

    fun setCannonVariant(variant: CannonOriginVariant) {
        _uiState.update { it.copy(cannonVariant = variant) }
        prefs.cannonVariant = variant
        recomputeValidation()
    }

    fun setFireMode(mode: FireMode) {
        _uiState.update { it.copy(fireMode = mode) }
        prefs.fireMode = mode
        recomputeValidation()
    }

    fun setTargetName(name: String) {
        _uiState.update { it.copy(targetName = name) }
        prefs.targetName = name
    }

    fun setOriginPreset(origin: Coordinates) {
        _uiState.update {
            it.copy(
                originXStr = origin.x.toInt().toString(),
                originYStr = origin.y.toInt().toString(),
                originZStr = origin.z.toInt().toString()
            )
        }
        recomputeValidation()
    }

    private fun validateState(state: FireControlUiState): ValidationResult {
        val formatErrors = state.inputFormatErrors()
        if (formatErrors.isNotEmpty()) {
            return ValidationResult(false, formatErrors)
        }
        return OscCalculatorEngine.validate(
            origin = state.origin,
            target = state.target,
            fireMode = state.fireMode,
            nukeSize = state.nukeSize,
            stabDepth = state.stabDepth,
            magazineSlot = state.magazineSlot,
            passcode = state.passcode,
            cannonOrigin = state.cannonVariant
        )
    }

    private fun recomputeValidation() {
        val validation = validateState(_uiState.value)
        _uiState.update { it.copy(validationResult = validation) }
    }

    fun calculateSolution() {
        dismissKeypad()
        val state = _uiState.value
        val validation = validateState(state)

        if (!validation.isValid) {
            _uiState.update {
                it.copy(
                    validationResult = validation,
                    errorMessage = validation.errors.firstOrNull()
                )
            }
            return
        }

        try {
            val result = OscCalculatorEngine.calculate(
                origin = state.origin,
                targetCoords = state.target,
                fireMode = state.fireMode,
                nukeSize = state.nukeSize,
                stabDepth = state.stabDepth,
                magazineSlot = state.magazineSlot,
                passcode = state.passcode,
                cannonOrigin = state.cannonVariant
            )

            // Save to persistent storage
            prefs.originX = state.originXStr
            prefs.originY = state.originYStr
            prefs.originZ = state.originZStr
            prefs.targetX = state.targetXStr
            prefs.targetY = state.targetYStr
            prefs.targetZ = state.targetZStr
            prefs.passcode = state.passcode
            prefs.nukeSize = state.nukeSize
            prefs.stabDepth = state.stabDepth
            prefs.magazineSlot = state.magazineSlot

            _uiState.update {
                it.copy(
                    calculationResult = result,
                    validationResult = validation,
                    errorMessage = null,
                    screenMode = ScreenMode.RESULTS
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(errorMessage = "Calculation failed: ${e.message}")
            }
        }
    }

    fun returnToInputs() {
        _uiState.update { it.copy(screenMode = ScreenMode.INPUT) }
    }
}
