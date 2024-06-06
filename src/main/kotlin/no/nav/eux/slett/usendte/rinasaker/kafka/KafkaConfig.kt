package no.nav.eux.slett.usendte.rinasaker.kafka

import no.nav.eux.slett.usendte.rinasaker.model.RinaDoc
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    val bootstrapServers: String,

    @Value("\${spring.kafka.properties.security.protocol}")
    val securityProtocol: String,

    @Value("\${spring.kafka.properties.ssl.keystore.type}")
    val keystoreType: String,

    @Value("\${spring.kafka.properties.ssl.keystore.location}")
    val keystoreLocation: String,

    @Value("\${spring.kafka.properties.ssl.keystore.password}")
    val keystorePassword: String,

    @Value("\${spring.kafka.properties.ssl.truststore.type}")
    val truststoreType: String,

    @Value("\${spring.kafka.properties.ssl.truststore.location}")
    val truststoreLocation: String,

    @Value("\${spring.kafka.properties.ssl.truststore.password}")
    val truststorePassword: String
) {

    @Bean
    fun docConsumerFactory(): ConsumerFactory<String, RinaDoc> =
        DefaultKafkaConsumerFactory(
            mapOf(
                BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                VALUE_DESERIALIZER_CLASS_CONFIG to ErrorHandlingDeserializer::class.java,
                ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS to StringDeserializer::class.java.name,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS to JsonDeserializer::class.java.name,
                JsonDeserializer.VALUE_DEFAULT_TYPE to RinaDoc::class.java.name,
                "security.protocol" to securityProtocol,
                "ssl.keystore.type" to keystoreType,
                "ssl.keystore.location" to keystoreLocation,
                "ssl.keystore.password" to keystorePassword,
                "ssl.truststore.type" to truststoreType,
                "ssl.truststore.location" to truststoreLocation,
                "ssl.truststore.password" to truststorePassword
            )
        )

    @Bean
    fun docKafkaListenerContainerFactory(
    ): ConcurrentKafkaListenerContainerFactory<String, RinaDoc> =
        ConcurrentKafkaListenerContainerFactory<String, RinaDoc>()
            .apply {
                consumerFactory = docConsumerFactory()
            }
}
