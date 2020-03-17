package uk.gov.dwp.dataworks.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.classic.spi.ThrowableProxy
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LogLayoutAppenderTest {
    @Test
    fun `doLayout returns empty when called on null`() {
        val result = LoggerLayoutAppender().doLayout(null)
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `doLayout returns skinny JSON on empty event`() {
        val result = LoggerLayoutAppender().doLayout(mock())
        assertJsonContainsCommonFields(result)
        assertJsonContainsField(result, "timestamp", "1970-01-01T00:00:00.000")
        assertJsonContainsField(result, "log_level", "null")
        assertJsonContainsField(result, "message", "null")
        assertJsonContainsField(result, "thread", "null")
        assertJsonContainsField(result, "logger", "null")
    }

    @Test
    fun `doLayout formats as JSON with vanilla message`() {
        val mockEvent = mock<ILoggingEvent>()
        whenever(mockEvent.timeStamp).thenReturn(9876543210)
        whenever(mockEvent.level).thenReturn(Level.WARN)
        whenever(mockEvent.threadName).thenReturn("my.thread.is.betty")
        whenever(mockEvent.loggerName).thenReturn("logger.name.is.mavis")
        whenever(mockEvent.formattedMessage).thenReturn("some message about stuff")
        whenever(mockEvent.hasCallerData()).thenReturn(false)
        val result = LoggerLayoutAppender().doLayout(mockEvent)
        println(result)

        assertJsonContainsCommonFields(result)
        assertJsonContainsField(result, "timestamp", "1970-04-25T07:29:03.210")
        assertJsonContainsField(result, "log_level", "WARN")
        assertJsonContainsField(result, "message", "some message about stuff")
        assertJsonContainsField(result, "thread", "my.thread.is.betty")
        assertJsonContainsField(result, "logger", "logger.name.is.mavis")
    }

    @Test
    fun `doLayout will flatten multiline messages`() {
        val mockEvent = mock<ILoggingEvent>()
        whenever(mockEvent.formattedMessage).thenReturn("some\nmessage\nabout\nstuff with\ttabs")
        whenever(mockEvent.hasCallerData()).thenReturn(false)

        val result = LoggerLayoutAppender().doLayout(mockEvent)
        println(result)

        assertJsonContainsCommonFields(result)
        assertJsonContainsField(result, "message", "some | message | about | stuff with tabs")
    }

    @Test
    fun `doLayout will format as json with embedded tuples`() {
        val mockEvent = mock<ILoggingEvent>()
        val embeddedTokens = semiFormattedTuples("some message about stuff", "key1" to "value1", "key2" to "value2")
        whenever(mockEvent.formattedMessage).thenReturn(embeddedTokens)
        whenever(mockEvent.hasCallerData()).thenReturn(false)

        val result = LoggerLayoutAppender().doLayout(mockEvent)
        println(result)
        assertJsonContainsCommonFields(result)
        assertJsonContainsField(result, "key1", "value1")
        assertJsonContainsField(result, "key2", "value2")
        assertJsonContainsField(result, "message", "some message about stuff")
    }

    @Test
    fun `doLayout should not escape json as that would mess with our custom static log methods which do`() {
        val mockEvent = mock<ILoggingEvent>()
        whenever(mockEvent.formattedMessage).thenReturn("message-/:'!@")
        whenever(mockEvent.hasCallerData()).thenReturn(false)

        val result = LoggerLayoutAppender().doLayout(mockEvent)
        assertJsonContainsField(result, "message", "message-/:'!@")
    }

    @Test
    fun `doLayout should add exceptions when provided`() {
        val mockEvent = mock<ILoggingEvent>()
        whenever(mockEvent.timeStamp).thenReturn(9876543210)
        whenever(mockEvent.level).thenReturn(Level.WARN)
        whenever(mockEvent.threadName).thenReturn("my.thread.is.betty")
        whenever(mockEvent.loggerName).thenReturn("logger.name.is.mavis")
        whenever(mockEvent.formattedMessage).thenReturn("some message about stuff")

        val stubThrowable = ThrowableProxy(catchMe1("i am an exception"))
        ThrowableProxyUtil.build(stubThrowable, catchMe2(), ThrowableProxy(catchMe3()))
        whenever(mockEvent.throwableProxy).thenReturn(stubThrowable as IThrowableProxy)

        val result = LoggerLayoutAppender().doLayout(mockEvent)
        assertThat(result).containsPattern("\"exception\":\".*\"")
        assertThat(result).contains("i am an exception")
    }

    private fun assertJsonContainsCommonFields(value: String) {
        LogField.values().forEach {
            assertThat(value).contains(it.propertyName)
        }
    }

    private fun assertJsonContainsField(json: String, key: String, value: String) {
        val parsed = ObjectMapper().readTree(json)
        val field = parsed.get(key)
        assertThat(field).isNotNull()
        assertThat(field.asText()).isEqualTo(value)
    }
}