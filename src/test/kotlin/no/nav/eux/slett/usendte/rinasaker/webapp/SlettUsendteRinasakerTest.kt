package no.nav.eux.slett.usendte.rinasaker.webapp

import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus.Status.*
import no.nav.eux.slett.usendte.rinasaker.webapp.dataset.kafkaRinaCase
import no.nav.eux.slett.usendte.rinasaker.webapp.dataset.kafkaRinaDocument
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.awaitility.kotlin.has
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.resttestclient.exchange
import org.springframework.http.HttpMethod.POST
import java.time.LocalDateTime.now

@TestMethodOrder(OrderAnnotation::class)
class SlettUsendteRinasakerTest : AbstractTest() {

    @Test
    @Order(1)
    fun `NOT_FOUND rapport uten treff sender ingen-melding`() {
        requestBodies.clear()
        restTemplate
            .exchange<Void>(
                "/api/v1/sletteprosess/not-found-rapport/execute",
                POST,
                httpEntity()
            )
        val slackBody = requestBodies["/slack/webhook"]
        assertThat(slackBody).isNotNull()
        assertThat(slackBody).contains("Ingen rinasaker satt til NOT_FOUND")
    }

    @Test
    @Order(2)
    fun `Nye rinasaker og dokumenter fra Kafka - sletting staged og eksekvert`() {
        assertThat(kafka.isRunning).isTrue
        assertThat(postgres.isRunning).isTrue
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(1))
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(2))
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(3))
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(4))
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(5))
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(6))
        await untilCallTo {
            rinasakStatusRepository.findAllByStatus(NY_SAK)
        } has {
            size == 6
        }
        kafkaTemplate.send("eessibasis.eux-rina-document-events-v1", kafkaRinaDocument(1))
        kafkaTemplate.send("eessibasis.eux-rina-document-events-v1", kafkaRinaDocument(2))
        await untilCallTo {
            rinasakStatusRepository.findAllByStatus(DOKUMENT_SENT)
        } has {
            size == 2
        }
        await untilCallTo { rinasakStatusRepository.findAllByStatus(NY_SAK) } has {
            size == 4
        }
        manipulerOpprettetTidspunkt()
        restTemplate
            .exchange<Void>(
                "/api/v1/sletteprosess/til-sletting/execute",
                POST,
                httpEntity()
            )
        println("Følgende requests ble utført i prosessen til sletting:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        assertThat(requestBodies["/api/v1/rinasaker/1/status"]).isNull()
        assertThat(requestBodies["/api/v1/rinasaker/2/status"]).isNull()
        assertThat(requestBodies["/api/v1/rinasaker/3/status"]).isNotNull()
        assertThat(requestBodies["/api/v1/rinasaker/4/status"]).isNull()
        assertThat(requestBodies["/api/v1/rinasaker/5/status"]).isNotNull()
        assertThat(requestBodies["/api/v1/rinasaker/6/status"]).isNotNull()
        assertThat(rinasakStatus(1)).isEqualTo(DOKUMENT_SENT)
        assertThat(rinasakStatus(2)).isEqualTo(DOKUMENT_SENT)
        assertThat(rinasakStatus(3)).isEqualTo(TIL_SLETTING)
        assertThat(rinasakStatus(4)).isEqualTo(NY_SAK)
        assertThat(rinasakStatus(5)).isEqualTo(KAN_IKKE_SLETTES)
        assertThat(rinasakStatus(6)).isEqualTo(TIL_SLETTING)
        restTemplate
            .exchange<Void>(
                "/api/v1/sletteprosess/slett/execute",
                POST,
                httpEntity()
            )
        println("Følgende requests ble utført i prosessen slett:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        assertThat(requestBodies["/api/v1/rinasaker/3"]).isNotNull()
        assertThat(requestBodies["/api/v1/rinasaker/6"]).isNotNull()
        assertThat(rinasakStatus(3)).isEqualTo(SLETTET)
        assertThat(rinasakStatus(6)).isEqualTo(NOT_FOUND)
        manipulerEndretTidspunktForNotFound()
        requestBodies.clear()
        restTemplate
            .exchange<Void>(
                "/api/v1/sletteprosess/not-found-rapport/execute",
                POST,
                httpEntity()
            )
        println("Følgende requests ble utført i prosessen not-found-rapport:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        val slackBody = requestBodies["/slack/webhook"]
        assertThat(slackBody).isNotNull()
        assertThat(slackBody).contains("NOT_FOUND")
        assertThat(slackBody).contains("6")
    }

    fun rinasakStatus(rinasakId: Int) = rinasakStatusRepository.findByRinasakId(rinasakId)!!.status

    fun manipulerOpprettetTidspunkt() {
        val medDokumentStatus = rinasakStatusRepository
            .findByRinasakId(1)!!
            .copy(opprettetTidspunkt = now().minusDays(32))
        rinasakStatusRepository.save(medDokumentStatus)
        val utenDokumentStatus = rinasakStatusRepository
            .findByRinasakId(3)!!
            .copy(opprettetTidspunkt = now().minusDays(32))
        rinasakStatusRepository.save(utenDokumentStatus)
        val rinasakKanIkkeSlettes = rinasakStatusRepository
            .findByRinasakId(5)!!
            .copy(opprettetTidspunkt = now().minusDays(32))
        rinasakStatusRepository.save(rinasakKanIkkeSlettes)
        val rinasakNotFound = rinasakStatusRepository
            .findByRinasakId(6)!!
            .copy(opprettetTidspunkt = now().minusDays(32))
        rinasakStatusRepository.save(rinasakNotFound)
    }

    fun manipulerEndretTidspunktForNotFound() {
        val notFoundSak = rinasakStatusRepository
            .findByRinasakId(6)!!
            .copy(endretTidspunkt = now().minusMonths(1).withDayOfMonth(15))
        rinasakStatusRepository.save(notFoundSak)
    }
}
