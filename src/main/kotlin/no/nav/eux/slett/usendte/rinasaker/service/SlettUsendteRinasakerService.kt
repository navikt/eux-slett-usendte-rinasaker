package no.nav.eux.slett.usendte.rinasaker.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus
import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus.Status.DOKUMENT_SENT
import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus.Status.NY_SAK
import no.nav.eux.slett.usendte.rinasaker.persistence.repository.RinasakStatusRepository
import org.springframework.stereotype.Service
import java.util.UUID.randomUUID

@Service
class SlettUsendteRinasakerService(
    val repository: RinasakStatusRepository
) {

    val log = logger {}

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