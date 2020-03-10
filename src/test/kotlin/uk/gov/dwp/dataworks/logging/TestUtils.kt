package uk.gov.dwp.dataworks.logging

fun catchMe1(message: String = ""): Throwable {
    try {
        MakeStacktrace1().callMe1(message)
    }
    catch (ex: Exception) {
        return ex
    }
    return RuntimeException("boom")
}

fun catchMe2(): Throwable {
    try {
        MakeStacktrace2().callMe2()
    }
    catch (ex: Exception) {
        return ex
    }
    return RuntimeException("boom")
}

fun catchMe3(): Throwable {
    try {
        MakeStacktrace3().callMe3()
    }
    catch (ex: Exception) {
        return ex
    }
    return RuntimeException("boom")
}

class MakeStacktrace1 {
    fun callMe1(message: String = "") {
        val exMessage = if(message != "") message else "boom1 - /:'!@£\$%^&*()"
        throw RuntimeException(exMessage)
    }
}

class MakeStacktrace2 {
    fun callMe2() {
        throw RuntimeException("boom2")
    }
}

class MakeStacktrace3 {
    fun callMe3() {
        throw RuntimeException("boom3")
    }
}
