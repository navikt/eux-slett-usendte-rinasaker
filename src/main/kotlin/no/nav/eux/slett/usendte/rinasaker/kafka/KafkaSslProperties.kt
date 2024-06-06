package no.nav.eux.slett.usendte.rinasaker.kafka

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.kafka.properties.ssl")
data class KafkaSslProperties(
    val keystore: StoreProperties,
    val truststore: StoreProperties
)

data class StoreProperties(
    val type: String,
    val location: String,
    val password: String
)
