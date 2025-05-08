package com.example.healthsystem.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREF_NAME = "HealthSystemPrefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoginInfo(context: Context, email: String, password: String) {
        getPrefs(context).edit().apply {
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun getLoginInfo(context: Context): Pair<String, String> {
        val prefs = getPrefs(context)
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val password = prefs.getString(KEY_PASSWORD, "") ?: ""
        return Pair(email, password)
    }

    fun clearLoginInfo(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
