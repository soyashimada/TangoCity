package nl.hva.tangocity.model

data class Card (
    val id: Int,
    val question: String,
    val answer: String,
    val repetition: Int = 0,
    val easiness_factor: Float,
    val interval: Int,
    val next_date: String,
    val last_result: Int
)
