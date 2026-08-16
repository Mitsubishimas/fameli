package com.fameli.budget.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("fameli_family", Context.MODE_PRIVATE)

    var currentFamilyId: String?
        get() = prefs.getString("family_id", null)
        set(value) {
            prefs.edit().putString("family_id", value).apply()
        }

    fun clear() {
        prefs.edit().remove("family_id").apply()
    }
}
