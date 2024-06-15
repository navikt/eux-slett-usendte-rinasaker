package no.nav.eux.slett.usendte.rinasaker.webapp.mock

import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

fun getEuxRinaTerminatorApiStatusResponseTrue() =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        setBody(getEuxRinaTerminatorApiStatusResponseBodyTrue)
    }

fun getEuxRinaTerminatorApiStatusResponseFalse() =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        setBody(getEuxRinaTerminatorApiStatusResponseBodyFalse)
    }

val getEuxRinaTerminatorApiStatusResponseBodyTrue =
    Any::class::class.java
        .getResource("/dataset/eux-rina-terminator-api-true.json")!!
        .readText()

val getEuxRinaTerminatorApiStatusResponseBodyFalse =
    Any::class::class.java
        .getResource("/dataset/eux-rina-terminator-api-false.json")!!
        .readText()
