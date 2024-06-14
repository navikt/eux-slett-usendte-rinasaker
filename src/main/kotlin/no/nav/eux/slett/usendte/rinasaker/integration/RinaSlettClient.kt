package no.nav.eux.slett.usendte.rinasaker.integration

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.body

@Service
class RinaSlettClient(
    @Value("\${endpoint.eux-rina-terminator-api}")
    val euxRinaTerminatorApiEndpoint: String,
    val euxRinaTerminatorApiRestTemplate: RestTemplate
) {

    val log = logger {}

    fun slettRinasak(rinasakId: Int) {
        euxRinaTerminatorApiRestTemplate
            .delete()
            .uri("${euxRinaTerminatorApiEndpoint}/api/v1/$rinasakId")
            .retrieve()
            .toBodilessEntity()
        log.info { "Sletter $rinasakId" }
    }

    fun kanSlettes(rinasakId: Int) = euxRinaTerminatorApiRestTemplate
        .get()
        .uri("${euxRinaTerminatorApiEndpoint}/api/v1/$rinasakId/status")
        .accept(APPLICATION_JSON)
        .retrieve()
        .body<EuxRinasakStatus>()
        ?.kanSlettes
        ?: throw KanSlettesException(rinasakId)

    data class EuxRinasakStatus(
        val kanSlettes: Boolean
    )

    class KanSlettesException(rinasakId: Int) : RuntimeException("Kan ikke hente status for $rinasakId")
}
