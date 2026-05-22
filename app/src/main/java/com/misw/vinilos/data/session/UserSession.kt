package com.misw.vinilos.data.session

import android.content.Context

object UserSession {
    private const val PREFS_NAME = "vinilos_session"
    private const val KEY_ROLE = "user_role"

    const val ROLE_COLLECTOR = "collector"
    const val ROLE_GUEST = "guest"

    fun getRole(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ROLE, null) ?: ROLE_GUEST

    fun setRole(context: Context, role: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_ROLE, role).apply()
    }

    fun isCollector(context: Context) = getRole(context) == ROLE_COLLECTOR

    fun hasRole(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .contains(KEY_ROLE)
}
