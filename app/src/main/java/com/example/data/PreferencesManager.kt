package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.CannonOriginVariant
import com.example.domain.Coordinates
import com.example.domain.FireMode

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("osc_fire_control_prefs", Context.MODE_PRIVATE)

    var originX: String
        get() = prefs.getString(KEY_ORIGIN_X, "0") ?: "0"
        set(value) = prefs.edit().putString(KEY_ORIGIN_X, value).apply()

    var originY: String
        get() = prefs.getString(KEY_ORIGIN_Y, "64") ?: "64"
        set(value) = prefs.edit().putString(KEY_ORIGIN_Y, value).apply()

    var originZ: String
        get() = prefs.getString(KEY_ORIGIN_Z, "0") ?: "0"
        set(value) = prefs.edit().putString(KEY_ORIGIN_Z, value).apply()

    var targetX: String
        get() = prefs.getString(KEY_TARGET_X, "1024") ?: "1024"
        set(value) = prefs.edit().putString(KEY_TARGET_X, value).apply()

    var targetY: String
        get() = prefs.getString(KEY_TARGET_Y, "68") ?: "68"
        set(value) = prefs.edit().putString(KEY_TARGET_Y, value).apply()

    var targetZ: String
        get() = prefs.getString(KEY_TARGET_Z, "-2048") ?: "-2048"
        set(value) = prefs.edit().putString(KEY_TARGET_Z, value).apply()

    var targetName: String
        get() = prefs.getString(KEY_TARGET_NAME, "Target 1") ?: "Target 1"
        set(value) = prefs.edit().putString(KEY_TARGET_NAME, value).apply()

    var passcode: Int
        get() = prefs.getInt(KEY_PASSCODE, 940)
        set(value) = prefs.edit().putInt(KEY_PASSCODE, value).apply()

    var cannonVariant: CannonOriginVariant
        get() {
            val id = prefs.getString(KEY_CANNON_VARIANT, CannonOriginVariant.OSC_MK6.id)
            return CannonOriginVariant.entries.find { it.id == id } ?: CannonOriginVariant.OSC_MK6
        }
        set(value) = prefs.edit().putString(KEY_CANNON_VARIANT, value.id).apply()

    var fireMode: FireMode
        get() {
            val id = prefs.getString(KEY_FIRE_MODE, FireMode.NUKE.id)
            return FireMode.entries.find { it.id == id } ?: FireMode.NUKE
        }
        set(value) = prefs.edit().putString(KEY_FIRE_MODE, value.id).apply()

    var nukeSize: Int
        get() = prefs.getInt(KEY_NUKE_SIZE, 10)
        set(value) = prefs.edit().putInt(KEY_NUKE_SIZE, value).apply()

    var stabDepth: Int
        get() = prefs.getInt(KEY_STAB_DEPTH, 10)
        set(value) = prefs.edit().putInt(KEY_STAB_DEPTH, value).apply()

    var magazineSlot: Int
        get() = prefs.getInt(KEY_MAGAZINE_SLOT, 0)
        set(value) = prefs.edit().putInt(KEY_MAGAZINE_SLOT, value).apply()

    companion object {
        private const val KEY_ORIGIN_X = "origin_x_str"
        private const val KEY_ORIGIN_Y = "origin_y_str"
        private const val KEY_ORIGIN_Z = "origin_z_str"
        private const val KEY_TARGET_X = "target_x_str"
        private const val KEY_TARGET_Y = "target_y_str"
        private const val KEY_TARGET_Z = "target_z_str"
        private const val KEY_TARGET_NAME = "target_name"
        private const val KEY_PASSCODE = "passcode"
        private const val KEY_CANNON_VARIANT = "cannon_variant"
        private const val KEY_FIRE_MODE = "fire_mode"
        private const val KEY_NUKE_SIZE = "nuke_size"
        private const val KEY_STAB_DEPTH = "stab_depth"
        private const val KEY_MAGAZINE_SLOT = "magazine_slot"
    }
}
