package nl.hva.tangocity.model

data class Card (
    val id: Int,
    val question: String,
    val answer: String,
    val repetition: Int = 0,
    val easinessFactor: Float,
    val interval: Int,
    val nextDate: String,
    val lastResult: Int
)
