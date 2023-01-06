package nl.hva.tangocity.model

import java.util.*

data class Card (
    var id: Int,
    var question: String,
    var answer: String,
    var repetition: Int = 0,
    var easinessFactor: Double,
    var interval: Int,
    var nextDate: Calendar,
    var lastResult: Int
)
