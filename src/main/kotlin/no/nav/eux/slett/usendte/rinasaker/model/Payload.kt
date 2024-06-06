package no.nav.eux.slett.usendte.rinasaker.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Payload(
    @JsonProperty("DOCUMENT_METADATA")
    val documentMetadata: DocumentMetadata
)