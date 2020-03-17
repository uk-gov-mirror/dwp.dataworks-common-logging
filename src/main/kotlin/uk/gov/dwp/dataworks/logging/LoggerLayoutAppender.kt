package uk.gov.dwp.dataworks.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.LayoutBase

/**
 * Implementation of [LayoutBase] which allows us to parse events and reformat into JSON structured messages. JSON
 * fields which are used in the log message are below.
 * ```
 * {
 *  "app_version": "",
 *  "application": "",
 *  "component": "",
 *  "correlation_id": "",
 *  "duration_in_milliseconds": "",
 *  "environment": "",
 *  "exception": "",
 *  "hostname": ""
 *  "log_level": "",
 *  "logger": "",
 *  "message": "",
 *  "thread": "",
 *  "timestamp": "",
 *  }
 * ```
 * Note that they may not be in the same order.
 */
class LoggerLayoutAppender : LayoutBase<ILoggingEvent>() {

    private var start_time_milliseconds = System.currentTimeMillis()

    private fun getDurationInMilliseconds(epochTime: Long): String {
        val elapsedMilliseconds = epochTime - start_time_milliseconds
        return elapsedMilliseconds.toString()
    }

    override fun doLayout(event: ILoggingEvent?): String {
        if (event == null) {
            return ""
        }
        val formattedTimestamp = epochToUTCString(event.timeStamp)
        val formattedMessage = flattenString(event.formattedMessage).trim('"')
        val formattedException = throwableProxyEventToJsonKeyPair(event)
        val formattedDuration = getDurationInMilliseconds(event.timeStamp)
        return """{"timestamp":"$formattedTimestamp", "log_level":"${event.level}", "message":"$formattedMessage", $formattedException"thread":"${event.threadName}", "logger":"${event.loggerName}", "duration_in_milliseconds":"$formattedDuration", ${LogFields.asJson}}"""
    }
}
