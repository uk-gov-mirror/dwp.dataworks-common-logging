package uk.gov.dwp.dataworks.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import org.apache.commons.text.StringEscapeUtils
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Formats a string with the following rules:
 * * Replaces line endings (Windows & Unix) with space delimited pipes (`" | "`)
 * * Replaces tab chars (`\t`) with a space
 * * If `text` is null return the literal string "null"
 */
fun flattenString(text: String?): String {
    return text
            ?.replace("\r\n", " | ")
            ?.replace("\n", " | ")
            ?.replace("\t", " ")
            ?: "null"
}

/**
 * Converts an [IThrowableProxy] object to a single line representation of it's Stack Trace. Returns a blank string if
 * `event` is empty.
 */
fun throwableProxyEventToJsonKeyPair(event: ILoggingEvent): String {
    val throwableProxy = event.throwableProxy ?: return ""

    val stackTrace = ThrowableProxyUtil.asString(throwableProxy)
    return "\"exception\":\"${StringEscapeUtils.escapeJson(flattenString(stackTrace))}\", "
}

/**
 * Converts a message and a set of Tuples to an _almost_ formatted Json string. Tuples are first parsed as follows:
 * ```
 *  TupleKey1":"TupleValue1", "TupleKey2":"TupleValue2"
 * ```
 * Then the input message is escaped as per [StringEscapeUtils.escapeJson] and prepended with quotes. The resulting
 * output will look like the following:
 * ```
 *  input message contents", "TupleKey1":"TupleValue1", "TupleKey2":"TupleValue2"
 * ```
 * For call which do not pass contents to [tuples], [message] contents will be escaped and returned.
 */
fun semiFormattedTuples(message: String, vararg tuples: Pair<String, String>): String {
    if (tuples.isEmpty()) {
        return StringEscapeUtils.escapeJson(message)
    }
    val formattedTuples = tuples.joinToString(
            separator = ", ",
            transform = { "\"${it.first}\":\"${StringEscapeUtils.escapeJson(it.second)}\"" })
    return "${StringEscapeUtils.escapeJson(message)}\", $formattedTuples"
}

private val dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss.SSS")
/**
 * Converts an epoch seconds [Long] to a String of format YYYY-MM-dd'T'HH:mm:ss.SSS
 */
fun epochToUTCString(epochTime: Long): String {
    return dtf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(epochTime), ZoneOffset.UTC))
}
