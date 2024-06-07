package no.nav.eux.slett.usendte.rinasaker

import no.nav.eux.slett.usendte.rinasaker.kafka.config.KafkaSslProperties
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableJwtTokenValidation(
    ignore = [
        "org.springframework",
        "org.springdoc"
    ]
)
@SpringBootApplication
@EnableConfigurationProperties(KafkaSslProperties::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
