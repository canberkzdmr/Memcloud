package com.cbo.memcloud.core.logger

import android.content.Context
import com.cbo.memcloud.core.logger.core.LogLevel
import com.cbo.memcloud.core.logger.core.LogMessage
import com.cbo.memcloud.core.logger.core.LoggerStrategy
import com.cbo.memcloud.core.logger.loggers.ConsoleLogger
import com.cbo.memcloud.core.logger.loggers.FileLogger

class Logger private constructor(
    private val strategies: List<LoggerStrategy>,
    private val autoGenerateTags: Boolean = true
) {
    fun debug(message: String, tag: String? = null) {
        log(LogLevel.DEBUG, message, tag, null)
    }

    fun info(message: String, tag: String? = null) {
        log(LogLevel.INFO, message, tag, null)
    }

    fun warning(message: String, tag: String? = null) {
        log(LogLevel.WARNING, message, tag, null)
    }

    fun error(message: String, tag: String? = null) {
        log(LogLevel.ERROR, message, tag, null)
    }

    fun debug(message: String, throwable: Throwable, tag: String? = null) {
        log(LogLevel.DEBUG, message, tag, throwable)
    }

    fun info(message: String, throwable: Throwable, tag: String? = null) {
        log(LogLevel.INFO, message, tag, throwable)
    }

    fun warning(message: String, throwable: Throwable, tag: String? = null) {
        log(LogLevel.WARNING, message, tag, throwable)
    }

    fun error(message: String, throwable: Throwable, tag: String? = null) {
        log(LogLevel.ERROR, message, tag, throwable)
    }

    private fun log(level: LogLevel, message: String, tag: String?, throwable: Throwable?) {
        val finalTag = tag ?: if (autoGenerateTags) getCallerTag() else null
        val callerInfo = if (autoGenerateTags) getCallerInfo() else null
        val logMessage = LogMessage(level, message, tag = finalTag, callerInfo = callerInfo, throwable = throwable)
        strategies.forEach { it.log(logMessage) }
    }

    private fun getCallerTag(): String {
        val stackTrace = Thread.currentThread().stackTrace
        return stackTrace.getOrNull(7)?.let {
            val className = it.className.substringAfterLast(".")
            "${className.substringBeforeLast("Kt")}.${it.methodName}"
        } ?: "Unknown"
    }

    private fun getCallerInfo(): String {
        val stackTrace = Thread.currentThread().stackTrace
        return stackTrace.getOrNull(4)?.let {
            "${it.fileName}:${it.lineNumber}"
        } ?: "Unknown"
    }

    class Builder {
        private val strategies = mutableListOf<LoggerStrategy>()
        private var autoGenerateTags = true

        fun addConsoleLogger(): Builder {
            strategies.add(ConsoleLogger())
            return this
        }

        fun addFileLogger(context: Context, fileName: String = "app_logs.txt"): Builder {
            strategies.add(FileLogger(context, fileName))
            return this
        }

        fun setAutoGenerateTags(enable: Boolean): Builder {
            autoGenerateTags = enable
            return this
        }

        fun build(): Logger {
            if (strategies.isEmpty()) {
                strategies.add(ConsoleLogger())
            }
            return Logger(strategies, autoGenerateTags)
        }
    }
}