package no.nav.eux.slett.usendte.rinasaker.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@Entity
data class RinasakStatus(
    @Id
    val rinasakStatusUuid: UUID,
    val rinasakId: Int,
    val status: String,
    @Column(updatable = false)
    val opprettetBruker: String,
    @Column(updatable = false)
    val opprettetTidspunkt: LocalDateTime,
    val endretBruker: String,
    val endretTidspunkt: LocalDateTime
)
