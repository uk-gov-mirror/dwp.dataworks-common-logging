package uk.gov.dwp.dataworks.logging

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.lang.IllegalArgumentException

class LogFieldsTest {
    @ParameterizedTest
    @EnumSource(LogField::class)
    fun `Common log fields returns values using LogField object`(logField: LogField) {
        val actual = LogFields.get(logField)
        assertThat(actual)
            .withFailMessage("Expected value of %s to be %s but was %s", logField.propertyName, logField.default, actual)
            .isEqualTo(logField.default)
    }

    @ParameterizedTest
    @EnumSource(LogField::class)
    fun `Common log fields returns values using String`(logField: LogField) {
        val actual = LogFields.get(logField.propertyName)
        assertThat(actual)
            .withFailMessage("Expected value of %s to be %s but was %s", logField.propertyName, logField.default, actual)
            .isEqualTo(logField.default)
    }

    @ParameterizedTest
    @EnumSource(LogField::class)
    fun `Common log fields are populated statically`(logField: LogField) {
        assertThat(LogFields.get(logField.propertyName)).isEqualTo(logField.default)
    }

    @ParameterizedTest
    @EnumSource(LogField::class)
    fun `LogFields statically populates JSON string`(logField: LogField) {
        assertThat(LogFields.asJson).contains(logField.propertyName)
        assertThat(LogFields.asJson).contains(logField.default)
        assertThat(LogFields.asJson).contains(""""${logField.propertyName}":"${logField.default}"""")
    }

    @Test
    fun `Log field can set and retrieve custom values`() {
        val expectedValue = "customValue"
        val expectedKey = "customKey"
        LogFields.put(expectedKey.toUpperCase(), expectedKey, expectedValue)
        assertThat(LogFields.get(expectedKey)).isEqualTo(expectedValue)
    }

    @Test
    fun `Log field throws exception when envVarName is blank`() {
        assertThrows<IllegalArgumentException> {  LogFields.put("", "sysKey", "value") }
    }
    @Test
    fun `Log field throws exception when systemPropName is blank`() {
        assertThrows<IllegalArgumentException> {  LogFields.put("envKey", "", "value") }
    }

    @Test
    fun `Log field throws exception when default is blank`() {
        assertThrows<IllegalArgumentException> {  LogFields.put("envKey", "sysKey", "") }
    }
}