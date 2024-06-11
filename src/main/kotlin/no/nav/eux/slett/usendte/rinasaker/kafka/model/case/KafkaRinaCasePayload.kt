package no.nav.eux.slett.usendte.rinasaker.kafka.model.case

import com.fasterxml.jackson.annotation.JsonProperty

data class KafkaRinaCasePayload(
    @JsonProperty("REST_CASE")
    val restCase: KafkaRinaCaseRestCase
)
