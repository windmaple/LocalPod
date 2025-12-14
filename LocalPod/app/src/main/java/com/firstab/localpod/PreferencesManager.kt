package com.firstab.localpod

import android.content.Context

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var autoplay: Boolean
        get() = sharedPreferences.getBoolean("autoplay", false)
        set(value) = sharedPreferences.edit().putBoolean("autoplay", value).apply()

    var skipSilence: Boolean
        get() = sharedPreferences.getBoolean("skip_silence", false)
        set(value) = sharedPreferences.edit().putBoolean("skip_silence", value).apply()

    var seekDuration: Int
        get() = sharedPreferences.getInt("seek_duration", 10)
        set(value) = sharedPreferences.edit().putInt("seek_duration", value).apply()
}
