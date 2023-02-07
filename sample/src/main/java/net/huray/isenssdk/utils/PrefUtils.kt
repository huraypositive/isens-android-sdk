package net.huray.isenssdk.utils

import android.annotation.SuppressLint
import android.content.Context
import net.huray.isenssdk.App.Companion.instance

@SuppressLint("CommitPrefEdits")
object PrefUtils {
    private val SHARED_PREFERENCES =
        instance.getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE)

    fun saveCareSensAddress(address: String?) {
        SHARED_PREFERENCES
            .edit()
            .putString(Const.PREF_CARE_SENS_ADDRESS, address)
            .apply()
    }

    fun getCareSensAddress(): String? {
        return SHARED_PREFERENCES.getString(Const.PREF_CARE_SENS_ADDRESS, null)
    }

    fun removeCareSensAddress() {
        SHARED_PREFERENCES
            .edit()
            .putString(Const.PREF_CARE_SENS_ADDRESS, null)
            .apply()
    }
}