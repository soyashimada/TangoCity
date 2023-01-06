package nl.hva.tangocity.model

data class Card (
    var id: Int,
    var question: String,
    var answer: String,
    var repetition: Int = 0,
    var easinessFactor: Double,
    var interval: Int,
    var nextDate: String,
    var lastResult: Int
)
