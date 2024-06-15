package no.nav.eux.journalarkivar.webapp.mock

import no.nav.eux.slett.usendte.rinasaker.webapp.mock.getEuxRinaTerminatorApiStatusResponseFalse
import no.nav.eux.slett.usendte.rinasaker.webapp.mock.getEuxRinaTerminatorApiStatusResponseTrue
import no.nav.eux.slett.usendte.rinasaker.webapp.mock.response204
import no.nav.eux.slett.usendte.rinasaker.webapp.mock.tokenResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType.TEXT_PLAIN

fun mockResponse(request: RecordedRequest, body: String) =
    when (request.method) {
        HttpMethod.POST.name() -> mockResponsePost(request, body)
        HttpMethod.GET.name() -> mockResponseGet(request)
        HttpMethod.PATCH.name() -> mockResponsePatch(request)
        HttpMethod.DELETE.name() -> mockResponseDelete(request)
        else -> defaultResponse()
    }

fun mockResponsePost(request: RecordedRequest, body: String) =
    when (request.uriEndsWith) {
        "/oauth2/v2.0/token" -> tokenResponse()
        "/api/v1/journalposter/settStatusAvbryt" -> response204()
        else -> defaultResponse()
    }

fun mockResponsePatch(request: RecordedRequest) =
    when (request.uriEndsWith) {
        else -> defaultResponse()
    }

fun mockResponseGet(request: RecordedRequest) =
    when (request.uriEndsWith) {
        "/api/v1/rinasaker/1/status" -> getEuxRinaTerminatorApiStatusResponseTrue()
        "/api/v1/rinasaker/2/status" -> getEuxRinaTerminatorApiStatusResponseTrue()
        "/api/v1/rinasaker/3/status" -> getEuxRinaTerminatorApiStatusResponseTrue()
        "/api/v1/rinasaker/4/status" -> getEuxRinaTerminatorApiStatusResponseTrue()
        "/api/v1/rinasaker/5/status" -> getEuxRinaTerminatorApiStatusResponseFalse()
        else -> defaultResponse()
    }

fun mockResponseDelete(request: RecordedRequest) =
    when (request.uriEndsWith) {
        "/api/v1/rinasaker/3" -> response204()
        else -> defaultResponse()
    }

fun defaultResponse() =
    MockResponse().apply {
        setHeader(CONTENT_TYPE, TEXT_PLAIN)
        setBody("no mock defined")
        setResponseCode(500)
    }

val RecordedRequest.uriEndsWith get() = requestUrl.toString().split("/mock")[1]
