package no.nav.eux.slett.usendte.rinasaker.kafka.model.case

data class KafkaRinaCase(
    val caseEventType: String,
    val payLoad: KafkaRinaCasePayload
)
