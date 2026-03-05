package no.nav.eux.slett.usendte.rinasaker.webapp

import com.nimbusds.jose.JOSEObjectType
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback

val MockOAuth2Server.token: String
    get() = this
        .issueToken("issuer1", "theclientid", defaultOAuth2TokenCallback)
        .serialize()

private val defaultOAuth2TokenCallback =
    DefaultOAuth2TokenCallback(
        "issuer1",
        "subject1",
        JOSEObjectType.JWT.type,
        listOf("demoapplication"),
        emptyMap(),
        3600
    )
