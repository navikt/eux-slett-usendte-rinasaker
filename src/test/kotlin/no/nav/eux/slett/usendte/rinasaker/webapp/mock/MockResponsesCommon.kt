package no.nav.eux.slett.usendte.rinasaker.webapp.mock

import okhttp3.mockwebserver.MockResponse

fun response204() =
    MockResponse().apply {
        setResponseCode(204)
    }

fun response404() =
    MockResponse().apply {
        setResponseCode(404)
        setBody("Feilet kall mot RINA: ")
    }
