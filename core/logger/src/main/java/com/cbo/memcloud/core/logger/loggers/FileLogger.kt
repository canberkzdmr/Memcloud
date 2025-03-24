package com.cbo.memcloud.core.logger.loggers

import android.annotation.SuppressLint
import android.content.Context
import com.cbo.memcloud.core.logger.core.LogMessage
import com.cbo.memcloud.core.logger.core.LoggerStrategy
import java.io.File
import java.util.Date

class FileLogger(
    private val context: Context,
    private val fileName: String = "memcloud_logs.txt"
) : LoggerStrategy {
    private val file: File by lazy {
        File(context.filesDir, fileName)
    }

    @SuppressLint("SimpleDateFormat")
    override fun log(message: LogMessage) {
        try {
            val formattedMessage = buildString {
                append(java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(message.timestamp)))
                append(" [${message.level}]")
                message.tag?.let { append(" [$it]") }
                append(": ${message.message}")
                message.callerInfo?.let { append(" ($it)") }
                message.throwable?.let { append("\n${it.stackTraceToString()}") }
                append("\n")
            }
            file.appendText(formattedMessage)
        } catch (e: Exception) {
            android.util.Log.e("FileLogger", "Failed to write log: ${e.message}")
        }
    }
}