package no.nav.eux.slett.usendte.rinasaker.model

data class RinaDoc(
    val documentEventType: String,
    val buc: String,
    val payLoad: Payload
)
