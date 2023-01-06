package nl.hva.tangocity.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.hva.tangocity.model.Card
import nl.hva.tangocity.model.Deck
import nl.hva.tangocity.repository.DeckRepository

class DeckViewModel(application: Application) : AndroidViewModel(application)  {
    private val TAG = "FIRESTORE"
    private val deckRepository: DeckRepository = DeckRepository()

    val decks: LiveData<ArrayList<Deck>> = deckRepository.decks

    private val _errorText: MutableLiveData<String> = MutableLiveData()
    val errorText: LiveData<String>
        get() = _errorText

    fun initialize() {
        viewModelScope.launch {
            try {
                deckRepository.initialize()
                if (decks.value == null) {
                    _errorText.value = "No deck is set"
                }
            } catch (ex: DeckRepository.RetrievalError) {
                val errorMsg = "Something went wrong while retrieving deck"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }

    fun createDeckAndCard(deckName: String, question: String, answer: String, callback: () -> Unit) {
        viewModelScope.launch {
            try {
                deckRepository.createDeckAndCard(deckName, question, answer)
                callback()
            } catch (ex: DeckRepository.SaveError) {
                val errorMsg = "Something went wrong while saving the deck"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }

    fun createCard(deckPosition: Int, question: String, answer: String, cardPosition: Int? = null, callback: () -> Unit) {
        viewModelScope.launch {
            try {
                val deck: Deck? = decks.value?.get(deckPosition)
                val deckId = deck?.id ?: 1
                val cardId = if (cardPosition != null)
                    deck?.cards?.get(cardPosition)?.id ?: 1 else null

                val newCardId = deckRepository.createCard(deckId, question, answer, cardId)

                deckRepository.decks.value?.let {
                    if (cardPosition != null && cardId != null){
                        it[deckPosition].cards[cardPosition].question = question
                        it[deckPosition].cards[cardPosition].answer = answer
                    }else {
                        it[deckPosition].cards.add(Card(newCardId, question, answer, 0,0.0,0,"0",0))
                    }
                }
                callback()
            } catch (ex: DeckRepository.SaveError) {
                val errorMsg = "Something went wrong while saving the card"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }
}