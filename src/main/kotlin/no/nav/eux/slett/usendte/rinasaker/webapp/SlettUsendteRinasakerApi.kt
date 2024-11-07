package no.nav.eux.slett.usendte.rinasaker.webapp

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import no.nav.eux.logging.clearLocalMdc
import no.nav.eux.logging.mdc
import no.nav.eux.slett.usendte.rinasaker.service.SlettUsendteRinasakerService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RestController

@Unprotected
@Validated
@RequestMapping("\${api.base-path:/api/v1}")
@RestController
class SlettUsendteRinasakerApi(
    val service: SlettUsendteRinasakerService
) {

    val log = logger {}

    @Operation(
        summary = "Slett usendte rinasaker API",
        operationId = "sletteprosess",
        description = "start sletteprosess for sletting av usendte rinasaker",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "No content"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized"
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden"
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    @RequestMapping(
        method = [POST],
        value = ["/sletteprosess/{sletteprosess}/execute"],
        produces = ["application/json"]
    )
    fun sletteprosess(
        @Parameter(
            description = """
                Navnet på prosessen som skal startes:   
                    * `til-sletting` - Markerer usendte rinasaker for sletting   
                    * `slett` - Utfører sletting mot Rina
                    """,
            required = true
        )
        @PathVariable("sletteprosess")
        sletteprosess: String
    ): ResponseEntity<Unit> {
        clearLocalMdc()
        mdc(sletteprosess = sletteprosess)
        log.info { "starter sletteprosess..." }
        when (sletteprosess) {
            "til-sletting" -> service.settUsendteRinasakerTilSletting()
            "slett" -> service.slettUsendteRinasaker()
            else -> {
                log.error { "ukjent sletteprosess: $sletteprosess" }
                return ResponseEntity(BAD_REQUEST)
            }
        }
        clearLocalMdc()
        log.info { "sletteprosess utført" }
        return ResponseEntity(NO_CONTENT)
    }

}
