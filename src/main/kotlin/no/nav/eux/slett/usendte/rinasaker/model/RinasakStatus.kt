package no.nav.eux.slett.usendte.rinasaker.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*

@Entity
data class RinasakStatus(
    @Id
    val rinasakStatusUuid: UUID,
    val rinasakId: Int,
    @Enumerated(STRING)
    val status: Status,
    val bucType: String,
    @Column(updatable = false)
    val opprettetBruker: String = "ukjent",
    @Column(updatable = false)
    val opprettetTidspunkt: LocalDateTime = now(),
    val endretBruker: String = "ukjent",
    val endretTidspunkt: LocalDateTime = now(),
) {
    enum class Status {
        NY_SAK, DOKUMENT_SENT
    }
}
