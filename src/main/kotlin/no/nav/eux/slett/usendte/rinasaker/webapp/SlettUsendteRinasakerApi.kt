package no.nav.eux.slett.usendte.rinasaker.webapp

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RestController

@Unprotected
@Validated
@RequestMapping("\${api.base-path:/api/v1}")
@RestController
class SlettUsendteRinasakerApi {

    val log = logger {}

    @Operation(
        summary = "Slett usendte rinasaker  API",
        operationId = "slettUsendteRinasaker",
        description = "start jobb for sletting av usendte rinasaker",
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
        value = ["/slettUsendteSaker"],
        produces = ["application/json"]
    )
    fun slettUsendteRinasaker(): ResponseEntity<Unit> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

}
