package no.nav.eux.slett.usendte.rinasaker.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus.Status.NOT_FOUND
import no.nav.eux.slett.usendte.rinasaker.persistence.repository.RinasakStatusRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

@Service
class NotFoundRapportService(
    val repository: RinasakStatusRepository,
    val slackService: SlackService
) {

    val log = logger {}

    fun sendNotFoundRapport() {
        val now = LocalDateTime.now()
        val forrigeMaanedStart = now.minusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay()
        val denneMaanedStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay()
        val maanedNavn = forrigeMaanedStart.month
            .getDisplayName(TextStyle.FULL, Locale.of("nb", "NO"))
        val aar = forrigeMaanedStart.year
        val notFoundSaker = repository
            .findAllByStatusAndEndretTidspunktBetween(NOT_FOUND, forrigeMaanedStart, denneMaanedStart)
        val melding = if (notFoundSaker.isEmpty()) {
            "Månedlig rapport: Ingen rinasaker satt til NOT_FOUND i $maanedNavn $aar"
        } else {
            val ids = notFoundSaker.joinToString(", ") { it.rinasakId.toString() }
            "Månedlig rapport: ${notFoundSaker.size} rinasaker satt til NOT_FOUND i $maanedNavn $aar: $ids"
        }
        log.info { melding }
        slackService.post(melding)
    }
}
