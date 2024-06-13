package no.nav.eux.slett.usendte.rinasaker.service

import org.slf4j.MDC
import java.util.*

fun <T> T.mdc(
    sletteprosess: String? = null,
    rinasakId: Int? = null,
    dokumentInfoId: String? = null,
    sedId: UUID? = null,
    sedVersjon: Int? = null,
    sedType : String? = null,
    bucType : String? = null,
    journalpostId: String? = null,
): T {
    "sletteprosess" leggTil sletteprosess
    "rinasakId" leggTil rinasakId
    "dokumentInfoId" leggTil dokumentInfoId
    "sedId" leggTil sedId
    "sedVersjon" leggTil sedVersjon
    "sedType" leggTil sedType
    "bucType" leggTil bucType
    "journalpostId" leggTil journalpostId
    return this
}

fun clearLocalMdc() {
    MDC.remove("sletteprosess")
    MDC.remove("rinaSakId")
    MDC.remove("dokumentInfoId")
    MDC.remove("sedId")
    MDC.remove("sedVersjon")
    MDC.remove("sedType")
    MDC.remove("bucType")
    MDC.remove("journalpostId")
}

fun <T> T.setAndClearLocalMdc(
    sletteprosess: String? = null,
    rinasakId: Int? = null,
    dokumentInfoId: String? = null,
    sedId: UUID? = null,
    sedVersjon: Int? = null,
    sedType : String? = null,
    bucType : String? = null,
    journalpostId: String? = null,
) {
    clearLocalMdc()
    mdc(
        sletteprosess = sletteprosess,
        rinasakId = rinasakId,
        dokumentInfoId = dokumentInfoId,
        sedId = sedId,
        sedVersjon = sedVersjon,
        sedType = sedType,
        bucType = bucType,
        journalpostId = journalpostId
    )
}

private infix fun String.leggTil(value: Any?) {
    if (value != null) MDC.put(this, "$value")
}
