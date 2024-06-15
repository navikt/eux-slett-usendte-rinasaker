package no.nav.eux.slett.usendte.rinasaker.webapp

import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus.Status.DOKUMENT_SENT
import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus.Status.NY_SAK
import no.nav.eux.slett.usendte.rinasaker.webapp.dataset.kafkaRinaCase
import no.nav.eux.slett.usendte.rinasaker.webapp.dataset.kafkaRinaDocument
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.awaitility.kotlin.has
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class SlettUsendteRinasakerKafkaTest: AbstractTest() {

    @Disabled("ikke ferdig")
    @Test
    fun `Nye rinasaker og dokumenter fra kafka topic`() {
        assertThat(kafka.isRunning).isTrue
        assertThat(postgres.isRunning).isTrue
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(1))
        await untilCallTo {
            rinasakStatusRepository.findByRinasakId(1)
        } has {
            rinasakId == 1 && status == NY_SAK
        }
        kafkaTemplate.send("eessibasis.eux-rina-document-events-v1", kafkaRinaDocument(1))
        await untilCallTo {
            rinasakStatusRepository.findByRinasakId(1)
        } has {
            rinasakId == 1 && status == DOKUMENT_SENT
        }
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(1))
        await untilCallTo { rinasakStatusRepository.findAllByStatus(NY_SAK)
        } has {
            isEmpty()
        }
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(2))
        await untilCallTo { rinasakStatusRepository.findAllByStatus(NY_SAK)
        } has {
            size == 1
        }
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(3))
        await untilCallTo { rinasakStatusRepository.findAllByStatus(NY_SAK)
        } has {
            size == 2
        }
        kafkaTemplate.send("eessibasis.eux-rina-document-events-v1", kafkaRinaDocument(1))
        kafkaTemplate.send("eessibasis.eux-rina-document-events-v1", kafkaRinaDocument(2))
        await untilCallTo { rinasakStatusRepository.findAllByStatus(NY_SAK) } has {
            size == 1
        }
        await untilCallTo {
            rinasakStatusRepository.findAllByStatus(DOKUMENT_SENT)
        } has {
            size == 2
        }
    }
}
