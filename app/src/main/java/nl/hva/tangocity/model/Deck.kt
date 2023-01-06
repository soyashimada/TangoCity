package nl.hva.tangocity.model

data class Deck (
    var id: Int,
    var name: String,
    var cards: ArrayList<Card>
)