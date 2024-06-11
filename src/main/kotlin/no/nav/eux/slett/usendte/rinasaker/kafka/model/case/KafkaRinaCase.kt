package no.nav.eux.slett.usendte.rinasaker.kafka.model.case

import no.nav.eux.slett.usendte.rinasaker.kafka.model.document.KafkaRinaDocumentPayload

data class KafkaRinaCase(
    val caseEventType: String,
    val payLoad: KafkaRinaDocumentPayload
)
