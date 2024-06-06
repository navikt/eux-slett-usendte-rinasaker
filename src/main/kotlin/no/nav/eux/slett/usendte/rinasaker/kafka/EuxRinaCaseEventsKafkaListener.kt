package no.nav.eux.slett.usendte.rinasaker.kafka

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class EuxRinaCaseEventsKafkaListener {

    val log = logger {}

    @KafkaListener(id = "eux-slett-usendte-rinasaker-case-draft-3", topics = ["eessibasis.eux-rina-case-events-v1"])
    fun case(value: String?) {
        log.info { "Received case kafka message: $value" }
    }

    @KafkaListener(id = "eux-slett-usendte-rinasaker-document-draft-3", topics = ["eessibasis.eux-rina-document-events-v1"])
    fun document(value: String?) {
        log.info { "Received document kafka message: $value" }
    }

    @KafkaListener(id = "eux-slett-usendte-rinasaker-document-draft-4", topics = ["eessibasis.eux-rina-document-events-v1"])
    fun document(doc: Doc) {
        log.info { "Received document kafka message (data class): $doc" }
    }
    
    @KafkaListener(id = "eux-slett-usendte-rinasaker-notification-draft-3", topics = ["eessibasis.eux-rina-notification-events-v1"])
    fun notification(value: String?) {
        log.info { "Received notification kafka message: $value" }
    }

    data class Doc(
        val documentEventType: String,
        val buc: String
    )
}
