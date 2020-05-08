package com.juntoscontracorona.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson


@SuppressLint("ApplySharedPref")
class PreferencesDataSource(context: Context) {

    private val gson by lazy { Gson() }

    private val sharedPref by lazy {
        context.getSharedPreferences("pref", Context.MODE_PRIVATE)
    }

    fun set(key: String, value: Any?) {
        val editor = sharedPref.edit()

        when (value) {
            null -> editor.remove(key)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Double -> throw UnsupportedOperationException("Double not supported")
            is String -> editor.putString(key, value)
            else -> editor.putString(key, gson.toJson(value))
        }

        editor.commit()
    }

    fun <T> get(key: String, classOfT: Class<T>): T? {
        if (!sharedPref.contains(key))
            return null

        return when (classOfT) {
            Boolean::class.java,
            java.lang.Boolean::class.java -> sharedPref.getBoolean(key, false) as T
            Int::class.java,
            java.lang.Integer::class.java -> sharedPref.getInt(key, 0) as T
            Long::class.java,
            java.lang.Long::class.java -> sharedPref.getLong(key, 0L) as T
            Float::class.java,
            java.lang.Float::class.java -> sharedPref.getFloat(key, 0f) as T
            String::class.java,
            java.lang.String::class.java -> sharedPref.getString(key, null) as T?
            else -> {
                val savedData = sharedPref.getString(key, null) ?: return null
                gson.fromJson<T>(savedData, classOfT)
            }
        }
    }
}