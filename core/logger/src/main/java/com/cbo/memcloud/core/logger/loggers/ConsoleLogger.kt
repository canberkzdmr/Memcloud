package com.cbo.memcloud.core.logger.loggers

import android.util.Log
import com.cbo.memcloud.core.logger.core.LogLevel
import com.cbo.memcloud.core.logger.core.LogMessage
import com.cbo.memcloud.core.logger.core.LoggerStrategy

class ConsoleLogger: LoggerStrategy {
    override fun log(message: LogMessage) {
        val formattedMessage= buildString {
            message.tag?.let { append("[$it]") }
            append(": ${message.message}")
            message.callerInfo?.let { append(" ($it)") }
            message.throwable?.let { append("\n${Log.getStackTraceString(it)}") }
        }

        when (message.level) {
            LogLevel.DEBUG -> Log.d(message.tag ?: "ConsoleLogger", formattedMessage, message.throwable)
            LogLevel.ERROR -> Log.e(message.tag ?: "ConsoleLogger", formattedMessage, message.throwable)
            LogLevel.INFO -> Log.i(message.tag ?: "ConsoleLogger", formattedMessage, message.throwable)
            LogLevel.WARNING -> Log.w(message.tag ?: "ConsoleLogger", formattedMessage, message.throwable)
        }
    }
}