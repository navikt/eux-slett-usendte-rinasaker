package no.nav.eux.slett.usendte.rinasaker.webapp.dataset

import no.nav.eux.slett.usendte.rinasaker.kafka.model.case.KafkaRinaCase
import no.nav.eux.slett.usendte.rinasaker.kafka.model.case.KafkaRinaCasePayload
import no.nav.eux.slett.usendte.rinasaker.kafka.model.case.KafkaRinaCaseRestCase
import no.nav.eux.slett.usendte.rinasaker.kafka.model.document.KafkaRinaDocument
import no.nav.eux.slett.usendte.rinasaker.kafka.model.document.KafkaRinaDocumentMetadata
import no.nav.eux.slett.usendte.rinasaker.kafka.model.document.KafkaRinaDocumentPayload

fun kafkaRinaCase(rinasakId: Int) = KafkaRinaCase(
    caseEventType = "OPEN_CASE",
    payLoad = KafkaRinaCasePayload(KafkaRinaCaseRestCase(rinasakId, "H_BUC_01"))
)

fun kafkaRinaDocument(rinasakId: Int) = KafkaRinaDocument(
    documentEventType = "SENT_DOCUMENT",
    buc = "H_BUC_01",
    payLoad = KafkaRinaDocumentPayload(KafkaRinaDocumentMetadata(rinasakId))
)
