package no.nav.eux.slett.usendte.rinasaker.kafka.model.document

data class KafkaRinaDocument(
    val documentEventType: String,
    val buc: String,
    val payLoad: KafkaRinaDocumentPayload
)
