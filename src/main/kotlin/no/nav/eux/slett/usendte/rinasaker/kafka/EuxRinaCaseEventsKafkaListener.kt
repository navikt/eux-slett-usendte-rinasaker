package no.nav.eux.slett.usendte.rinasaker.kafka

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class EuxRinaCaseEventsKafkaListener {

    val log = logger {}

    @KafkaListener(id = "eux-slett-usendte-rinasaker-case-draft-3", topics = ["eessibasis.eux-rina-case-events-v1"])
    fun case(value: String?) {
        log.info { "Received case kafka message: $value" }
    }

    @KafkaListener(
        id = "eux-slett-usendte-rinasaker-document-draft-3",
        topics = ["eessibasis.eux-rina-document-events-v1"]
    )
    fun document(value: String?) {
        log.info { "Received document kafka message: $value" }
    }

    @KafkaListener(
        id = "eux-slett-usendte-rinasaker-document-draft-4",
        topics = ["eessibasis.eux-rina-document-events-v1"]
    )
    fun document(consumerRecord: ConsumerRecord<String, Doc>) {
        log.info { "Received document kafka message, caseId: ${consumerRecord.value().payLoad.documentMetadata.caseId}" }
        log.info { "Received document kafka message (data class): $consumerRecord, doc=${consumerRecord.value()}" }
    }

    @KafkaListener(
        id = "eux-slett-usendte-rinasaker-notification-draft-3",
        topics = ["eessibasis.eux-rina-notification-events-v1"]
    )
    fun notification(value: String?) {
        log.info { "Received notification kafka message: $value" }
    }

    data class Doc(
        val documentEventType: String,
        val buc: String,
        val payLoad: Payload
    )

    data class Payload(
        @JsonProperty("DOCUMENT_METADATA")
        val documentMetadata: DocumentMetadata
    )

    data class DocumentMetadata(
        val caseId: String
    )
}
