package uk.gov.dwp.dataworks.logging

import java.net.InetAddress
/**
 * Object to hold the Application specific and Common fields that should be added to each of the log lines output by
 * this library. Provides helper methods to return these fields as a set of json compliant Key:value pairs but without
 * any wrapping (i.e no `{}`).
 *
 * `asJson` is both a `var` and function as this allows eager computation of the message. This removes the a performance
 * overhead where it would be computed at log write time. This _should not_ be edited by the user.
 */
object LogFields {
    private val logFields = CommonLogFields.commonFields.toMutableMap()
    var asJson = asJson()

    private fun asJson(): String {
        return logFields.map { "\"${it.key}\":\"${it.value}\"" }.joinToString(separator = ", ")
    }

    fun get(logField: String): String {
        return logFields.getValue(logField)
    }

    fun get(logField: LogField): String {
        return get(logField.propertyName)
    }

    /**
     * Add a custom logField to this library. [systemPropName] will be used as the key.
     */
    fun put(envVarName: String, systemPropName: String, default: String) {
        if(envVarName.isBlank() || systemPropName.isBlank() || default.isBlank()) {
            throw IllegalArgumentException("Parameters must not be blank. Got: envVarName:'$envVarName', systemPropName: '$systemPropName', default: '$default'.")
        }
        logFields[systemPropName] = resolveValueFromSystem(envVarName, systemPropName, default)
        asJson = asJson()
    }
}

/**
 * Object which represents fields that are common across all applications. These are extracted from Environment or
 * Java System variables and used to prepend all log lines sent by this library.
 */
object CommonLogFields {
    val commonFields: Map<String, String> =
        LogField.values().associate {
            val value = resolveValueFromSystem(it.name, it.propertyName, it.default)
            Pair(it.propertyName, value)
        }
}

enum class LogField(val propertyName: String, val default: String) {
    ENVIRONMENT("environment", "NOT_SET"),
    APPLICATION("application", "NOT_SET"),
    APP_VERSION("app_version", "NOT_SET"),
    COMPONENT("component", "NOT_SET"),
    CORRELATION_ID("correlation_id", "NOT_SET"),
    HOSTNAME("hostname", InetAddress.getLocalHost().hostName);
}

/**
 * Resolve a value from the following places, in preferential order:
 * * Environment variable, both upper and lower case - [System.getenv]
 * * System property, both upper and lower case - [System.getProperty]
 * * [LogField.default]
 *
 * @param envVarName name of the environment var to search for
 * @param systemPropName name of the Java system property to search for
 * @param default value to set to if not resolved.
 */
fun resolveValueFromSystem(envVarName: String, systemPropName: String, default: String): String {
    return System.getenv(envVarName)
            ?: System.getenv(systemPropName)
            ?: System.getProperty(envVarName)
            ?: System.getProperty(systemPropName)
            ?: default
}
