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
    val bootstrapServers: String
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
                JsonDeserializer.VALUE_DEFAULT_TYPE to RinaDoc::class.java.name
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
