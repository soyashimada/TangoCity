package nl.hva.tangocity.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import nl.hva.tangocity.model.Card
import nl.hva.tangocity.model.Deck
import java.util.*
import kotlin.collections.ArrayList

class DeckRepository {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var deckDocument =
        firestore.collection("Decks")
    private var cardDocument =
        firestore.collection("Cards")

    private val lastDeckId: MutableLiveData<Int> = MutableLiveData(1)
    private val lastCardId: MutableLiveData<Int> = MutableLiveData(1)

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
                deckData.sortedBy { deck -> deck.id }

                deckData.forEach {
                    val id = it.id
                    val name = it.get("name").toString()
                    val cardsInDeck :ArrayList<Card> = arrayListOf()

                    cardData.forEach{ card ->
                        val cardId = card.id
                        if ( card.get("deckRef") == it.reference ){
                            val question = card.get("question").toString()
                            val answer = card.get("answer").toString()
                            val repetition = card.get("repetition").toString()
                            val easinessFactor = card.get("easinessFactor").toString()
                            val interval = card.get("interval").toString()
                            val nextDate = card.get("nextDate") as Timestamp
                            val lastResult = card.get("lastResult").toString()

                            val nextCal: Calendar = Calendar.getInstance()
                            nextCal.time = nextDate.toDate()
                            nextCal.set(Calendar.MILLISECOND, 0)
                            nextCal.set(Calendar.SECOND, 0)
                            nextCal.set(Calendar.MINUTE, 0)
                            nextCal.set(Calendar.HOUR_OF_DAY, 0)

                            cardsInDeck.add( Card( cardId.toInt(), question, answer,
                                repetition.toInt(), easinessFactor.toDouble(),
                                interval.toInt(), nextCal, lastResult.toInt() ))
                        }
                    }
                    cardsInDeck.sortBy { card -> card.id }
                    if ((lastCardId.value ?: 1) < cardsInDeck[cardsInDeck.size - 1].id){
                        lastCardId.value = cardsInDeck[cardsInDeck.size - 1].id
                    }

                    deckList += Deck(id.toInt(), name, cardsInDeck)
                    deckList.sortBy { deck -> deck.id }
                }

                lastDeckId.value = deckList[deckList.size -1].id
                _decks.value = deckList
            }
        } catch (e: Exception) {
            throw RetrievalError("Retrieval-firebase-task was unsuccessful")
        }
    }

    suspend fun createDeckAndCard(deckName: String, question: String, answer: String) {
        try {
            val deckId = createDeck(deckName)
            createCard(deckId, question, answer)
        } catch (e: Exception) {
            throw SaveError(e.message.toString(), e)
        }
    }

    private suspend fun createDeck(deckName: String) : Int {
        try {
            val id = lastDeckId.value?.plus(1) ?: 1
            val deck = hashMapOf(
                "name" to deckName
            )

            withTimeout(5_000) {
                deckDocument
                    .document(id.toString())
                    .set(deck)
                    .await()

                _createSuccess.value = true
                lastDeckId.value = id
            }

            return id
        } catch (e: Exception) {
            throw SaveError(e.message.toString(), e)
        }
    }

    suspend fun createCard(
        deckId: Int,
        question: String,
        answer: String,
        cardId: Int? = null,
        ease: Double = 0.0,
        interval: Int = 0,
        lastResult: Int = 0,
        nextDate: Timestamp = Timestamp.now(),
        repetition: Int = 0
    ): Int {
        try {
            val id = cardId ?: (lastCardId.value?.plus(1) ?: 1)
            val card = hashMapOf(
                "question" to question,
                "answer" to answer,
                "deckRef" to deckDocument.document(deckId.toString()),
                "easinessFactor" to ease,
                "interval" to interval,
                "lastResult" to lastResult,
                "nextDate" to nextDate,
                "repetition" to repetition
            )

            withTimeout(5_000) {
                cardDocument
                    .document(id.toString())
                    .set(card)
                    .await()

                _createSuccess.value = true
                lastCardId.value = id
            }

            return id
        } catch (e: Exception) {
            throw SaveError(e.message.toString(), e)
        }
    }

    class SaveError(message: String, cause: Throwable) : Exception(message, cause)
    class RetrievalError(message: String) : Exception(message)
}