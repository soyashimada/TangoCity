package nl.hva.tangocity.model

import java.util.*

data class Review (
    var id: Int,
    var deckId: Int,
    var cardId: Int,
    var result: Int,
    var date: Calendar
)