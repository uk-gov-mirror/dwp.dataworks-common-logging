package uk.gov.dwp.dataworks.logging

import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

class DataworksLoggerTest {
    @Test
    fun `Debug will format partial JSON`() {
        val mockLogger: org.slf4j.Logger = mock()
        whenever(mockLogger.isDebugEnabled).thenReturn(true)

        DataworksLogger(mockLogger).debug("main-message", "key1" to "value1", "key2" to "value2")

        verify(mockLogger, times(1)).isDebugEnabled
        verify(mockLogger, times(1)).debug("\"main-message\", \"key1\":\"value1\", \"key2\":\"value2\"")
        verifyNoMoreInteractions(mockLogger)
    }

    @Test
    fun `Info will format partial JSON`() {
        val mockLogger: org.slf4j.Logger = mock()
        whenever(mockLogger.isInfoEnabled).thenReturn(true)

        DataworksLogger(mockLogger).info("main-message", "key1" to "value1", "key2" to "value2")

        verify(mockLogger, times(1)).isInfoEnabled
        verify(mockLogger, times(1)).info("\"main-message\", \"key1\":\"value1\", \"key2\":\"value2\"")
        verifyNoMoreInteractions(mockLogger)
    }

    @Test
    fun `Error will format partial JSON`() {
        val mockLogger: org.slf4j.Logger = mock()
        whenever(mockLogger.isErrorEnabled).thenReturn(true)

        DataworksLogger(mockLogger).error("main-message", "key1" to "value1", "key2" to "value2")

        verify(mockLogger, times(1)).isErrorEnabled
        verify(mockLogger, times(1)).error("\"main-message\", \"key1\":\"value1\", \"key2\":\"value2\"")
        verifyNoMoreInteractions(mockLogger)
    }

    @Test
    fun `Error will format partial JSON When called with key-value pairs and exception`() {
        val mockLogger: org.slf4j.Logger = mock()
        whenever(mockLogger.isErrorEnabled).thenReturn(true)
        val exception = RuntimeException("boom")

        DataworksLogger(mockLogger).error("main-message", exception, "key1" to "value1", "key2" to "value2")

        verify(mockLogger, times(1)).isErrorEnabled
        verify(mockLogger, times(1)).error(eq("\"main-message\", \"key1\":\"value1\", \"key2\":\"value2\""), same(exception))
        verifyNoMoreInteractions(mockLogger)
    }
}