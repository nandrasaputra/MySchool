package com.nandra.myschool.ui.main.account

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.nandra.myschool.R

class SettingPreferenceFragment : PreferenceFragmentCompat()/*, SharedPreferences.OnSharedPreferenceChangeListener*/ {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey)
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        PreferenceManager.getDefaultSharedPreferences(activity)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        PreferenceManager.getDefaultSharedPreferences(activity)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when(key) {
            PREFERENCE_KEY_DARK_MODE -> {
                //val value = sharedPreferences?.getString(key, PREFERENCE_VALUE_ENTRIES_DARK_MODE_OFF) ?: PREFERENCE_VALUE_ENTRIES_DARK_MODE_OFF
                setDarkModeValue(PREFERENCE_VALUE_ENTRIES_DARK_MODE_OFF)
            }
        }
    }

    private fun setDarkModeValue(value: String) {
        when(value) {
            PREFERENCE_VALUE_ENTRIES_DARK_MODE_ON -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            PREFERENCE_VALUE_ENTRIES_DARK_MODE_OFF -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            PREFERENCE_VALUE_ENTRIES_DARK_MODE_AUTO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    companion object {
        const val PREFERENCE_KEY_DARK_MODE = "preference_key_dark_mode"
        const val PREFERENCE_VALUE_ENTRIES_DARK_MODE_AUTO = "dark_auto"
        const val PREFERENCE_VALUE_ENTRIES_DARK_MODE_ON = "dark_on"
        const val PREFERENCE_VALUE_ENTRIES_DARK_MODE_OFF = "dark_off"
    }*/
}