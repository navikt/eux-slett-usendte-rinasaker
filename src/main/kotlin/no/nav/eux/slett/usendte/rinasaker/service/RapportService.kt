package no.nav.eux.slett.usendte.rinasaker.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus.Status.*
import no.nav.eux.slett.usendte.rinasaker.persistence.repository.RinasakStatusRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

@Service
class RapportService(
    val repository: RinasakStatusRepository,
    val slackService: SlackService
) {

    val log = logger {}

    fun sendRapport() {
        val now = LocalDateTime.now()
        val forrigeMaanedStart = now.minusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay()
        val denneMaanedStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay()
        val maanedNavn = forrigeMaanedStart.month
            .getDisplayName(TextStyle.FULL, Locale.of("nb", "NO"))
        val aar = forrigeMaanedStart.year
        val melding = buildString {
            append("*Månedlig rapport*\n")
            append("_${maanedNavn} ${aar}_\n\n")
            appendForrigeMaaned(forrigeMaanedStart, denneMaanedStart)
            append("\n")
            appendNaaværendeKoe()
        }
        log.info { melding }
        slackService.post(melding)
    }

    private fun StringBuilder.appendForrigeMaaned(
        fra: LocalDateTime,
        til: LocalDateTime
    ) {
        val slettet = repository.countByStatusAndEndretTidspunktBetween(SLETTET, fra, til)
        val notFound = repository.countByStatusAndEndretTidspunktBetween(NOT_FOUND, fra, til)
        val slettingFeilet = repository.countByStatusAndEndretTidspunktBetween(SLETTING_FEILET, fra, til)
        val kanIkkeSlettes = repository.countByStatusAndEndretTidspunktBetween(KAN_IKKE_SLETTES, fra, til)
        val dokumentSendt = repository.countByStatusAndEndretTidspunktBetween(DOKUMENT_SENT, fra, til)
        append("*Forrige måned:*\n")
        append("• Slettet: $slettet\n")
        if (notFound > 0) append("• Not found: $notFound\n")
        if (slettingFeilet > 0) append("• Sletting feilet: $slettingFeilet\n")
        if (kanIkkeSlettes > 0) append("• Kan ikke slettes: $kanIkkeSlettes\n")
        if (dokumentSendt > 0) append("• Dokument mottatt: $dokumentSendt\n")
    }

    private fun StringBuilder.appendNaaværendeKoe() {
        val nyeSaker = repository.countByStatus(NY_SAK)
        val tilSletting = repository.countByStatus(TIL_SLETTING)
        val venterPaaRetry = repository.countByStatus(SLETTING_FEILET_RETRY)
        append("*Nåværende kø:*\n")
        append("• Nye saker: $nyeSaker\n")
        append("• Til sletting: $tilSletting\n")
        append("• Venter på retry: $venterPaaRetry")
    }
}
