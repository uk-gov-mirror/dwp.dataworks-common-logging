[![dwp](https://circleci.com/gh/dwp/dataworks-common-logging.svg?style=shield)](https://app.circleci.com/pipelines/github/dwp/dataworks-common-logging)

# dataworks-common-logging
Kotlin utility library to be used in Dataworks applications to ensure common logging format.

## Using in your project
JAR files for this project are published to this repositories [GitHub Packages page](https://github.com/dwp/dataworks-common-logging/packages). To include it in your project, follow the [official github instructions](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages). Configuration should look similar to the below.

#### Inclusion via Gradle
```kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/dwp/dataworks-common-logging")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}
```
Where `gpr.user` or `USERNAME` resolves to your GitHub username and `gpr.key` or `TOKEN` resolves to a GitHub PAT code with the `read:packages` privilege.

#### Logger configuration
To utilise this library in your project, you will need to include the compiled this projects`.jar` file. You are also required to add a logback XML file in the resources for your project, and add the following code as an `appender`. This will inform the logging framework to use `LoggerLayoutAppender` to parse messages into our format.
```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <!-- encoders are assigned the type ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
        <layout class="app.utils.logging.LoggerLayoutAppender"/>
    </encoder>
</appender>
```

## Log formatting
Dataworks common logging provides opinionated ways to write messages into log files, using [sl4j](http://www.slf4j.org/).

Out of the box, it provides functionality to convert log messages to JSON and appends a number of common fields. These can be found in the `LogField` enum class. Any `LogField` which is not found at runtime gets set to a default value, also defined in that enum. Some example variable values are as follows:

| Variable       | Example value  |
|----------------|----------------|
| environment    | development    |
| application    | my-special-api |
| app_version    | v1             |
| component      | read-from-x    |
| correlation_id | 1              |
| hostname       | 127.0.0.1      |

## Custom fields in log messages
For the case where you would like to add custom values to the logging lines which are output, `LogFields` provides functionality to do this.
```kotlin
LogFields.put("custom_env_var", "default value")
val customLogField = LogFields.get("custom_env_var")
```
This functionality is ideal for use cases where an application would like to present information which is specific to its running.

Values associated to the custom variables are resolved in the same manner as common fields, i.e:
1. Environment variable with the specified name
2. Java System property with the specified name
3. Default value provided to `LogFields.put()`

Log lines output with custom fields look like the following (without pretty printing):
```json
{
  "timestamp": "1970-04-25T07:29:03.210",
  "log_level": "WARN",
  "message": "some message about stuff",
  "customKey1": "Custom value 1",
  "customKey2": "Custom value 2",
  "thread": "my.thread.is.betty",
  "logger": "logger.name.is.mavis",
  "duration_in_milliseconds": "-1573885286308",
  "environment": "test",
  "application": "tests",
  "app_version": "v1",
  "component": "tests",
  "correlation_id": "1",
  "hostname": "127.0.0.1"
}
```

## Example log calls
Below are some examples of how you can use the library to output logs.

##### Including a DataworksLogger in a class
```kotlin
companion object {
    val logger = DataworksLogger.getLogger("thisClass")
}
``` 

##### Writing a log at DEBUG for data output program
```kotlin
val logger = DataworksLogger.getLogger("thisClass")

// Using Kotlin-like syntax (preferred):
logger.debug("Written manifest", "attempt_number" to "${attempts + 1}", "manifest_size" to "$manifestSize", "s3_location" to "s3://$manifestBucket/$manifestPrefix/$manifestFileName")

// Using Java-like syntax
logger.debug("Written manifest",  Pair("attempt_number", "${attempts + 1}"), Pair("manifest_size", "$manifestSize"), Pair("s3_location", "s3://$manifestBucket/$manifestPrefix/$manifestFileName"))
``` 

## Outputs
Some example outputs are displayed below. Note that in actuality these will be on a single line, the formatting here is shown for ease of reading.
##### Fully populated
```json
{
  "timestamp": "1970-04-25T07:29:03.210",
  "log_level": "WARN",
  "message": "some message about stuff",
  "thread": "my.thread.is.betty",
  "logger": "logger.name.is.mavis",
  "duration_in_milliseconds": "-1573885286308",
  "environment": "test",
  "application": "tests",
  "app_version": "v1",
  "component": "tests",
  "correlation_id": "1",
  "hostname": "127.0.0.1"
}
```

##### With thrown exception
```json
{
  "timestamp": "1970-04-25T07:29:03.210",
  "log_level": "WARN",
  "message": "some message about stuff",
  "exception": "java.lang.RuntimeException: boom1 - \/:'!@\u00A3$%^&*() |  at <omitted> |  at <omitted> |  ... 89 common frames omitted",
  "thread": "my.thread.is.betty",
  "logger": "logger.name.is.mavis",
  "duration_in_milliseconds": "-1573885286308",
  "environment": "test",
  "application": "tests",
  "app_version": "v1",
  "component": "tests",
  "correlation_id": "1",
  "hostname": "127.0.0.1"
}
```
