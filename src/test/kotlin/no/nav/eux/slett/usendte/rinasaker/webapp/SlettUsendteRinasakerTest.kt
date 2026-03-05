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
import java.time.LocalDateTime.now

@TestMethodOrder(OrderAnnotation::class)
class SlettUsendteRinasakerTest : AbstractTest() {

    @Test
    @Order(1)
    fun `Rapport uten treff sender tom rapport`() {
        requestBodies.clear()
        restTestClient
            .post()
            .uri("/api/v1/sletteprosess/rapport/execute")
            .headers { it.setBearerAuth(mockOAuth2Server.token) }
            .exchange()
            .expectStatus().isNoContent
        val slackBody = requestBodies["/slack/webhook"]
        assertThat(slackBody).isNotNull()
        assertThat(slackBody).contains("Månedlig rapport")
        assertThat(slackBody).contains("Slettet: 0")
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
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", kafkaRinaCase(7))
        await untilCallTo {
            rinasakStatusRepository.findAllByStatus(NY_SAK)
        } has {
            size == 7
        }
        kafkaTemplate.send("eessibasis.eux-rina-document-events-v1", kafkaRinaDocument(1))
        kafkaTemplate.send("eessibasis.eux-rina-document-events-v1", kafkaRinaDocument(2))
        await untilCallTo {
            rinasakStatusRepository.findAllByStatus(DOKUMENT_SENT)
        } has {
            size == 2
        }
        await untilCallTo { rinasakStatusRepository.findAllByStatus(NY_SAK) } has {
            size == 5
        }
        manipulerOpprettetTidspunkt()
        restTestClient
            .post()
            .uri("/api/v1/sletteprosess/til-sletting/execute")
            .headers { it.setBearerAuth(mockOAuth2Server.token) }
            .exchange()
            .expectStatus().isNoContent
        println("Følgende requests ble utført i prosessen til sletting:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        assertThat(requestBodies["/api/v1/rinasaker/1/status"]).isNull()
        assertThat(requestBodies["/api/v1/rinasaker/2/status"]).isNull()
        assertThat(requestBodies["/api/v1/rinasaker/3/status"]).isNotNull()
        assertThat(requestBodies["/api/v1/rinasaker/4/status"]).isNull()
        assertThat(requestBodies["/api/v1/rinasaker/5/status"]).isNotNull()
        assertThat(requestBodies["/api/v1/rinasaker/6/status"]).isNotNull()
        assertThat(requestBodies["/api/v1/rinasaker/7/status"]).isNotNull()
        assertThat(rinasakStatus(1)).isEqualTo(DOKUMENT_SENT)
        assertThat(rinasakStatus(2)).isEqualTo(DOKUMENT_SENT)
        assertThat(rinasakStatus(3)).isEqualTo(TIL_SLETTING)
        assertThat(rinasakStatus(4)).isEqualTo(NY_SAK)
        assertThat(rinasakStatus(5)).isEqualTo(KAN_IKKE_SLETTES)
        assertThat(rinasakStatus(6)).isEqualTo(TIL_SLETTING)
        assertThat(rinasakStatus(7)).isEqualTo(TIL_SLETTING)
        restTestClient
            .post()
            .uri("/api/v1/sletteprosess/slett/execute")
            .headers { it.setBearerAuth(mockOAuth2Server.token) }
            .exchange()
            .expectStatus().isNoContent
        println("Følgende requests ble utført i prosessen slett:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        assertThat(requestBodies["/api/v1/rinasaker/3"]).isNotNull()
        assertThat(requestBodies["/api/v1/rinasaker/6"]).isNotNull()
        assertThat(requestBodies["/api/v1/rinasaker/7"]).isNotNull()
        assertThat(rinasakStatus(3)).isEqualTo(SLETTET)
        assertThat(rinasakStatus(6)).isEqualTo(NOT_FOUND)
        assertThat(rinasakStatus(7)).isEqualTo(SLETTING_FEILET_RETRY)
        restTestClient
            .post()
            .uri("/api/v1/sletteprosess/slett/execute")
            .headers { it.setBearerAuth(mockOAuth2Server.token) }
            .exchange()
            .expectStatus().isNoContent
        assertThat(rinasakStatus(7)).isEqualTo(SLETTING_FEILET)
        manipulerEndretTidspunktForRapport()
        requestBodies.clear()
        restTestClient
            .post()
            .uri("/api/v1/sletteprosess/rapport/execute")
            .headers { it.setBearerAuth(mockOAuth2Server.token) }
            .exchange()
            .expectStatus().isNoContent
        println("Følgende requests ble utført i prosessen rapport:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        val slackBody = requestBodies["/slack/webhook"]
        assertThat(slackBody).isNotNull()
        assertThat(slackBody).contains("Månedlig rapport")
        assertThat(slackBody).contains("Slettet: 1")
        assertThat(slackBody).contains("Not found: 1")
        assertThat(slackBody).contains("Sletting feilet: 1")
        assertThat(slackBody).contains("Kan ikke slettes: 1")
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
        val rinasakSlettingFeiler = rinasakStatusRepository
            .findByRinasakId(7)!!
            .copy(opprettetTidspunkt = now().minusDays(32))
        rinasakStatusRepository.save(rinasakSlettingFeiler)
    }

    fun manipulerEndretTidspunktForRapport() {
        val forrigeMaaned = now().minusMonths(1).withDayOfMonth(15)
        listOf(3, 5, 6, 7).forEach { rinasakId ->
            val sak = rinasakStatusRepository
                .findByRinasakId(rinasakId)!!
                .copy(endretTidspunkt = forrigeMaaned)
            rinasakStatusRepository.save(sak)
        }
    }
}
