package no.nav.eux.slett.usendte.rinasaker.kafka.model

import com.fasterxml.jackson.annotation.JsonProperty

data class KafkaRinaDocumentPayload(
    @JsonProperty("DOCUMENT_METADATA")
    val documentMetadata: KafkaRinaDocumentMetadata
)
