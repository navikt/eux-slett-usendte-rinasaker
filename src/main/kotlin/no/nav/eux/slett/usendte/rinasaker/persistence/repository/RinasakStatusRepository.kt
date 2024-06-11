package no.nav.eux.slett.usendte.rinasaker.persistence.repository

import no.nav.eux.slett.usendte.rinasaker.model.RinasakStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RinasakStatusRepository : JpaRepository<RinasakStatus, UUID> {
    fun findByRinasakId(rinasakId: Int): RinasakStatus
}
