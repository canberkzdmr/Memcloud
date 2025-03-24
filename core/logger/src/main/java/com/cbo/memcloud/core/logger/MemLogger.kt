package com.cbo.memcloud.core.logger

object MemLogger {
    private var logger: Logger? = null

    fun initialize(builder: Logger.Builder.() -> Unit) {
        logger = Logger.Builder().apply(builder).build()
    }

    fun d(message: String, tag: String? = null) = logger?.debug(message, tag) ?: Unit
    fun e(message: String, tag: String? = null) = logger?.error(message, tag) ?: Unit
    fun i(message: String, tag: String? = null) = logger?.info(message, tag) ?: Unit
    fun w(message: String, tag: String? = null) = logger?.warning(message, tag) ?: Unit

    fun d(message: String, tag: String? = null, throwable: Throwable) = logger?.debug(message, throwable, tag) ?: Unit
    fun e(message: String, tag: String? = null, throwable: Throwable) = logger?.error(message, throwable, tag) ?: Unit
    fun i(message: String, tag: String? = null, throwable: Throwable) = logger?.info(message, throwable, tag) ?: Unit
    fun w(message: String, tag: String? = null, throwable: Throwable) = logger?.warning(message, throwable, tag) ?: Unit
}