package no.nav.eux.slett.usendte.rinasaker.kafka.listener

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.logging.mdc
import no.nav.eux.slett.usendte.rinasaker.kafka.model.case.KafkaRinaCase
import no.nav.eux.slett.usendte.rinasaker.kafka.model.document.KafkaRinaDocument
import no.nav.eux.slett.usendte.rinasaker.service.SlettUsendteRinasakerService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.lang.Thread.sleep

@Service
class EuxRinaCaseEventsKafkaListener(
    val service: SlettUsendteRinasakerService
) {

    val log = logger {}

    @KafkaListener(
        id = "eux-slett-usendte-rinasaker-case",
        groupId = "eux-slett-usendte-rinasaker-case",
        topics = ["\${kafka.topics.eux-rina-case-events-v1}"],
        containerFactory = "rinaCaseKafkaListenerContainerFactory"
    )
    fun case(consumerRecord: ConsumerRecord<String, KafkaRinaCase>) {
        val caseId = consumerRecord.value().payLoad.restCase.id
        val processDefinitionName = consumerRecord.value().payLoad.restCase.processDefinitionName
        mdc(rinasakId = caseId, bucType = processDefinitionName)
        log.info { "Mottok rina case event" }
        try {
            service.leggTilSak(rinasakId = caseId, bucType = processDefinitionName)
        } catch (e: DataIntegrityViolationException) {
            sleep(1000)
            log.info(e) { "Feil ved lagring av sak, forsøker igjen" }
            service.leggTilSak(rinasakId = caseId, bucType = processDefinitionName)
            log.info { "Lagt til sak ok ved retry" }
        } catch (e: Exception) {
            log.error(e) { "Feil ved lagring av sak" }
            throw e
        }
    }

    @KafkaListener(
        id = "eux-slett-usendte-rinasaker-document",
        groupId = "eux-slett-usendte-rinasaker-document",
        topics = ["\${kafka.topics.eux-rina-document-events-v1}"],
        containerFactory = "rinaDocumentKafkaListenerContainerFactory"
    )
    fun document(consumerRecord: ConsumerRecord<String, KafkaRinaDocument>) {
        val documentEventType = consumerRecord.value().documentEventType
        val caseId = consumerRecord.value().payLoad.documentMetadata.caseId
        val bucType = consumerRecord.value().buc
        mdc(rinasakId = caseId, bucType = bucType)
        log.info { "Mottok rina document event" }
        when (documentEventType) {
            "SENT_DOCUMENT" -> leggTilDokument(caseId, bucType)
            "RECEIVE_DOCUMENT" -> leggTilDokument(caseId, bucType)
            else -> log.info { "Mottok documentEventType, ignorerer: $documentEventType" }
        }
    }

    fun leggTilDokument(caseId: Int, bucType: String) =
        try {
            service.leggTilDokument(rinasakId = caseId, bucType = bucType)
        } catch (e: DataIntegrityViolationException) {
            sleep(1000)
            log.info(e) { "Feil ved lagring av dokument, forsøker igjen" }
            service.leggTilDokument(rinasakId = caseId, bucType = bucType)
            log.info { "Dokument info ok ved retry" }
        } catch (e: Exception) {
            log.error(e) { "Feil ved lagring av dokument" }
            throw e
        }

}
