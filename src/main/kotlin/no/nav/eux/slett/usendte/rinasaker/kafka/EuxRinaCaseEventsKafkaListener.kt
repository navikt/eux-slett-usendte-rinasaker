package no.nav.eux.slett.usendte.rinasaker.kafka

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class EuxRinaCaseEventsKafkaListener {

    val log = logger {}

    @KafkaListener(id = "eux-slett-usendte-rinasaker-draft-1", topics = ["eux-rina-case-events-v1"])
    fun listen(value: String?) {
        log.info { "Received kafka message: $value" }
    }
}
