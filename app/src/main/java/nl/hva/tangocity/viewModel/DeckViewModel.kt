package nl.hva.tangocity.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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
}