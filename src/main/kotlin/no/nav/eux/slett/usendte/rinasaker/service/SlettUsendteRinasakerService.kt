package no.nav.eux.slett.usendte.rinasaker.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.slett.usendte.rinasaker.integration.RinaSlettClient
import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus
import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus.Status.*
import no.nav.eux.slett.usendte.rinasaker.persistence.repository.RinasakStatusRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import java.util.UUID.randomUUID

@Service
class SlettUsendteRinasakerService(
    val rinaSlettClient: RinaSlettClient,
    val repository: RinasakStatusRepository
) {

    val log = logger {}

    fun slettUsendteRinasaker() {
        repository
            .findAllByStatus(TIL_SLETTING)
            .forEach { it.trySlett() }
    }

    fun settUsendteRinasakerTilSletting() {
        repository
            .findAllByStatusAndOpprettetTidspunktBefore(NY_SAK, now().minusDays(1))
            .forEach { it.settTilSletting() }
    }

    fun RinasakStatus.trySlett() =
        try {
            mdc(rinasakId = rinasakId, bucType = bucType)
            slett()
        } catch (e: Exception) {
            log.error(e) { "Kunne ikke slette rinasak $rinasakId" }
        }

    fun RinasakStatus.slett() {
        mdc(rinasakId = rinasakId, bucType = bucType)
        rinaSlettClient.slettRinaSak(rinasakId)
        log.info { "Rinasak slettet" }
        repository.save(copy(status = SLETTET, endretTidspunkt = now()))
        log.info { "Rinasak status oppdatert" }
    }

    fun RinasakStatus.settTilSletting() {
        mdc(rinasakId = rinasakId, bucType = bucType)
        log.info {
            "Rinasak $rinasakId ble opprettet $opprettetTidspunkt og har " +
                    "ingen dokumenter, rinasak status blir derfor satt til sletting"
        }
        repository.save(copy(status = TIL_SLETTING, endretTidspunkt = now()))
    }

    fun leggTilDokument(rinasakId: Int, bucType: String) {
        val rinasakStatus = repository.findByRinasakId(rinasakId)
        repository.save(rinasakStatus.copy(status = DOKUMENT_SENT))
        log.info { "Dokument lagt til" }
    }

    fun leggTilSak(rinasakId: Int, bucType: String) {
        val rinasakStatus = rinasakStatus(rinasakId, bucType, NY_SAK)
        repository.save(rinasakStatus)
        log.info { "Sak lagt til" }
    }

    fun rinasakStatus(
        rinasakId: Int,
        bucType: String,
        status: RinasakStatus.Status
    ) =
        RinasakStatus(
            rinasakStatusUuid = randomUUID(),
            rinasakId = rinasakId,
            status = status,
            bucType = bucType
        )
}