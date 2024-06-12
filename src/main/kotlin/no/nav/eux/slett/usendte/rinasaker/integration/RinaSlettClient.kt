package no.nav.eux.slett.usendte.rinasaker.integration

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.stereotype.Service

@Service
class RinaSlettClient {

    val log = logger {}

    fun slettRinaSak(rinaSakId: Int) {
        log.info { "Sletter $rinaSakId" }
    }
}
