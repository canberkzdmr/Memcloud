package com.cbo.memcloud.core.logger.core

interface LoggerStrategy {
    fun log(message: LogMessage)
}