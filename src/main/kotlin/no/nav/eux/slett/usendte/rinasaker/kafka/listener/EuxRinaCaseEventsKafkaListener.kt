package no.nav.eux.slett.usendte.rinasaker.kafka.listener

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.slett.usendte.rinasaker.kafka.model.case.KafkaRinaCase
import no.nav.eux.slett.usendte.rinasaker.kafka.model.document.KafkaRinaDocument
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class EuxRinaCaseEventsKafkaListener {

    val log = logger {}

    @KafkaListener(
        id = "eux-slett-usendte-rinasaker-case-draft-5",
        topics = ["eessibasis.eux-rina-case-events-v1"],
        containerFactory = "rinaCaseKafkaListenerContainerFactory"
    )
    fun case(consumerRecord: ConsumerRecord<String, KafkaRinaCase>) {
        log.info { "Received case kafka message, caseId: ${consumerRecord.value().payLoad.documentMetadata.caseId}" }
        log.info { "Received case kafka message (data class): $consumerRecord, case=${consumerRecord.value()}" }
    }

    @KafkaListener(
        id = "eux-slett-usendte-rinasaker-document-draft-5",
        topics = ["\${kafka.topics.eux-rina-document-events-v1}"],
        containerFactory = "rinaDocumentKafkaListenerContainerFactory"
    )
    fun document(consumerRecord: ConsumerRecord<String, KafkaRinaDocument>) {
        log.info { "Received document kafka message, caseId: ${consumerRecord.value().payLoad.documentMetadata.caseId}" }
        log.info { "Received document kafka message (data class): $consumerRecord, doc=${consumerRecord.value()}" }
    }

}
