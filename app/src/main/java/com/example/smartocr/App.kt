package com.example.smartocr

import android.app.Application
import com.example.smartocr.util.SharePreferenceExt
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SharePreferenceExt.SharePreferences.init(this)
    }
}