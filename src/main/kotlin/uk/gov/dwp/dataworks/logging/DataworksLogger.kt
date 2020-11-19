package uk.gov.dwp.dataworks.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * Entry class to this library. Essentially wraps an existing logger and injects common fields and provided tuples as
 * JSON such that intelligent log parsing can be carried out on them (e.g Splunk, CloudWatch insights etc.)
 */
class DataworksLogger(private val delegateLogger: Logger) {

    companion object {
        fun getLogger(forClassName: String): DataworksLogger {
            val slf4jLogger: Logger = LoggerFactory.getLogger(forClassName)
            return DataworksLogger(slf4jLogger)
        }

        fun <T : Any> getLogger(clazz: KClass<T>): DataworksLogger {
            val slf4jLogger: Logger = LoggerFactory.getLogger(clazz.java)
            return DataworksLogger(slf4jLogger)
        }

        fun <T> getLogger(clazz: Class<T>): DataworksLogger {
            val slf4jLogger: Logger = LoggerFactory.getLogger(clazz)
            return DataworksLogger(slf4jLogger)
        }
    }

    fun isDebugEnabled() = delegateLogger.isDebugEnabled

    fun debug(message: String, vararg tuples: Pair<String, String>) {
        if (delegateLogger.isDebugEnabled) {
            val semiFormatted = semiFormattedTuples(message, *tuples)
            delegateLogger.debug(semiFormatted)
        }
    }

    fun info(message: String, vararg tuples: Pair<String, String>) {
        if (delegateLogger.isInfoEnabled) {
            val semiFormatted = semiFormattedTuples(message, *tuples)
            delegateLogger.info(semiFormatted)
        }
    }

    fun warn(message: String, vararg tuples: Pair<String, String>) {
        if (delegateLogger.isWarnEnabled) {
            val semiFormatted = semiFormattedTuples(message, *tuples)
            delegateLogger.warn(semiFormatted)
        }
    }

    fun error(message: String, vararg tuples: Pair<String, String>) {
        if (delegateLogger.isErrorEnabled) {
            val semiFormatted = semiFormattedTuples(message, *tuples)
            delegateLogger.error(semiFormatted)
        }
    }

    fun error(message: String, error: Throwable, vararg tuples: Pair<String, String>) {
        if (delegateLogger.isErrorEnabled) {
            val semiFormatted = semiFormattedTuples(message, *tuples)
            delegateLogger.error(semiFormatted, error)
        }
    }
}
