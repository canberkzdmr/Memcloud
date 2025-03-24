package com.cbo.memcloud.core.logger.core

data class LogMessage(
    val level: LogLevel,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String? = null,
    val callerInfo: String? = null,
    val throwable: Throwable? = null,
)
