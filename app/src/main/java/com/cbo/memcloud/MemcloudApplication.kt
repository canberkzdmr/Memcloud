package com.cbo.memcloud

import android.app.Application
import com.cbo.memcloud.core.logger.MemLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MemcloudApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MemLogger.initialize {
            addConsoleLogger()
            addFileLogger(this@MemcloudApplication)
            setAutoGenerateTags(true)
        }
    }
}