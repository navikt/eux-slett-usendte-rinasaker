package no.nav.eux.slett.usendte.rinasaker.kafka.model

data class KafkaRinaDocument(
    val documentEventType: String,
    val buc: String,
    val payLoad: KafkaRinaDocumentPayload
)
