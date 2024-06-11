package no.nav.eux.slett.usendte.rinasaker.service

import org.springframework.stereotype.Service

@Service
class SlettUsendteRinasakerService {

    infix fun leggTilDokumentFor(rinasakId: Int) {
        println("Dokument lagt til")
    }

    infix fun leggTilSak(rinasakId: Int) {
        println("Case lagt til")
    }
}