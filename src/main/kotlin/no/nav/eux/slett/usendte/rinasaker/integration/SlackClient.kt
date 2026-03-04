package no.nav.eux.slett.usendte.rinasaker.integration

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class SlackClient(
    @Value("\${slack.webhooks.driftsoppfolging}")
    val webhookEndpoint: String
) {

    val log = logger {}

    fun post(text: String) {
        val response = RestClient.create()
            .post()
            .uri(webhookEndpoint)
            .contentType(APPLICATION_JSON)
            .body(SlackMessage(text))
            .retrieve()
            .toEntity(String::class.java)
        log.info { "Slack message sent: ${response.body}" }
    }

    data class SlackMessage(val text: String)
}
