package com.example.smartocr.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson


object SharePreferenceExt {
    var username: String
        get() = SharePreferences.valueOf("user_name", "")
        set(value) {
            SharePreferences.setValue("user_name", value)
        }

    @JvmStatic
    var password
        get(): String {
            return SharePreferences.valueOf("password", "")
        }
        set(value) {
            SharePreferences.setValue("password", value)
        }

    var lastDomain
        get(): String {
            return SharePreferences.valueOf("last_domain", "192.168.31.124")
        }
        set(value) {
            SharePreferences.setValue("last_domain", value)
        }

    object SharePreferences {

        private const val NAME = "prox_share_preference"
        private const val MODE = Context.MODE_PRIVATE
        lateinit var preferences: SharedPreferences
            private set

        private var initialed: Boolean = false

        @JvmStatic
        fun init(context: Context) {
            preferences = context.getSharedPreferences(NAME, MODE)
            initialed = true
        }

        inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
            val editor = edit()
            operation(editor)
            editor.apply()
        }

        inline fun <reified T> valueOf(key: String, defaultValue: T): T {
            when (defaultValue) {
                is Boolean -> {
                    return preferences.getBoolean(key, defaultValue) as T
                }

                is Int -> {
                    return preferences.getInt(key, defaultValue) as T
                }

                is Float -> {
                    return preferences.getFloat(key, defaultValue) as T
                }

                is Long -> {
                    return preferences.getLong(key, defaultValue) as T
                }

                is String -> {
                    return preferences.getString(key, defaultValue) as T
                }

                else -> {

                    val json = preferences.getString(key, "")
                    if (json!!.isBlank()) return defaultValue
                    return Gson().fromJson(json, T::class.java)
                }
            }
        }


        inline fun <reified T> setValue(key: String, value: T) {
            when (value) {
                is Boolean -> {
                    preferences.edit {
                        it.putBoolean(key, value)
                    }
                }

                is Int -> {
                    preferences.edit {
                        it.putInt(key, value)
                    }
                }

                is Float -> {
                    preferences.edit {
                        it.putFloat(key, value)
                    }
                }

                is Long -> {
                    preferences.edit {
                        it.putLong(key, value)
                    }
                }

                is String -> {
                    preferences.edit {
                        it.putString(key, value)
                    }
                }

                else -> {
                    preferences.edit {
                        it.putString(key, Gson().toJson(value))
                    }
                }
            }
        }
    }
}
