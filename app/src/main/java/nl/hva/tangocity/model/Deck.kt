package nl.hva.tangocity.model

data class Deck (
    val id: Int,
    val name: String,
    val cards: ArrayList<Card>
)