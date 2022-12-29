package nl.hva.tangocity.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import nl.hva.tangocity.model.Card
import nl.hva.tangocity.model.Deck

class DeckRepository {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var deckDocument =
        firestore.collection("Decks")
    private var cardDocument =
        firestore.collection("Cards")

    private val lastDeckId: MutableLiveData<Int> = MutableLiveData(2)
    private val lastCardId: MutableLiveData<Int> = MutableLiveData(2)

    private val _decks: MutableLiveData<ArrayList<Deck>> = MutableLiveData()
    val decks: LiveData<ArrayList<Deck>>
        get() = _decks

    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val createSuccess: LiveData<Boolean>
        get() = _createSuccess

    suspend fun initialize() {
        try {
            withTimeout(5_000) {
                val deckData = deckDocument
                    .get()
                    .await()

                val cardData = cardDocument
                    .get()
                    .await()

                val deckList = arrayListOf<Deck>()

//                deckData.sortedBy { deck -> deck.id }
                deckData.forEach {
                    val id = it.id
                    val name = it.getString("name").toString()
                    val cardsInDeck :ArrayList<Card> = arrayListOf()

                    cardData.forEach{ card ->
                        val cardId = card.id
                        if ( card.get("deck") == it.reference ){
                            val question = card.get("question").toString()
                            val answer = card.get("answer").toString()
                            val repetition = card.get("repetition").toString()
                            val easinessFactor = card.get("easiness_factor").toString()
                            val interval = card.get("interval").toString()
                            val nextDate = card.get("next_date").toString()
                            val lastResult = card.get("last_result").toString()

                            cardsInDeck.add( Card( cardId.toInt(), question, answer,
                                repetition.toInt(), easinessFactor.toFloat(),
                                interval.toInt(), nextDate, lastResult.toInt() ))
                        }
                    }

                    deckList += Deck(id.toInt(), name, cardsInDeck)
                    deckList.sortBy { deck -> deck.id }
                }

                _decks.value = deckList
            }
        } catch (e: Exception) {
            Log.e("test", e.toString())
            throw RetrievalError("Retrieval-firebase-task was unsuccessful")
        }
    }

    suspend fun createDeck(deck: Deck) {
        try {
            val id = lastDeckId.value!!.plus(1)
            withTimeout(5_000) {
                deckDocument
                    .document(id.toString())
                    .set(deck)
                    .await()

                _createSuccess.value = true
                lastDeckId.value = id
            }
        } catch (e: Exception) {
            throw SaveError(e.message.toString(), e)
        }
    }

    suspend fun createCard(card: Card) {
        try {
            val id = lastCardId.value!!.plus(1)
            withTimeout(5_000) {
                cardDocument
                    .document(id.toString())
                    .set(card)
                    .await()

                _createSuccess.value = true
                lastCardId.value = id
            }
        } catch (e: Exception) {
            throw SaveError(e.message.toString(), e)
        }
    }

    class SaveError(message: String, cause: Throwable) : Exception(message, cause)
    class RetrievalError(message: String) : Exception(message)
}