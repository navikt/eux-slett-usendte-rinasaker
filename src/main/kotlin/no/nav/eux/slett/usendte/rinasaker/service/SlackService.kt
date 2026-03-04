package no.nav.eux.slett.usendte.rinasaker.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.slett.usendte.rinasaker.integration.SlackClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SlackService(
    val slackClient: SlackClient,
    @Value("\${NAIS_APP_NAME:slett-usendte-rinasaker}")
    appName: String
) {

    val log = logger {}

    val environment: String = when {
        appName.endsWith("q1") -> "q1"
        appName.endsWith("q2") -> "q2"
        else -> "prod"
    }

    fun post(tekst: String) {
        try {
            slackClient.post("`[$environment]` `[slett-usendte-rinasaker]` $tekst")
        } catch (e: RuntimeException) {
            log.error(e) { "Feilet å sende Slack-melding: $tekst" }
        }
    }
}
