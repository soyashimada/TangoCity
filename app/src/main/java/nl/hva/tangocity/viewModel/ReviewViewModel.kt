package nl.hva.tangocity.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nl.hva.tangocity.model.Card
import java.util.*
import kotlin.collections.ArrayList

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val _studyCards: MutableLiveData<ArrayList<Card>> = MutableLiveData()
    val studyCards: LiveData<ArrayList<Card>>
        get() = _studyCards

    private val _currentCardSide: MutableLiveData<Boolean> = MutableLiveData(false)
    val currentCardSide: LiveData<Boolean>
        get() = _currentCardSide

    private val _studyPosition: MutableLiveData<Int> = MutableLiveData(0)
    val studyPosition : LiveData<Int>
        get() = _studyPosition

    private val _selectedTab: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTab : LiveData<Int>
        get() = _selectedTab

    private val _selectedDeckPosition: MutableLiveData<Int> = MutableLiveData(0)
    val selectedDeckPosition : LiveData<Int>
        get() = _selectedDeckPosition

    private val _selectedCardPosition: MutableLiveData<Int?> = MutableLiveData()
    val selectedCardPosition : LiveData<Int?>
        get() = _selectedCardPosition

    fun updateStudyCards(cards: ArrayList<Card>?, callback: () -> Unit = {}){
        _studyCards.value = arrayListOf()
        cards?.forEach{
            if (it.nextDate <= Calendar.getInstance()) {
                _studyCards.value?.add(it)
            }
        }
        callback()
    }

    fun resetStudyPosition() {
        _studyPosition.value = 0
    }

    fun showAnswer() {
        _currentCardSide.value = true
    }

    fun hideAnswer() {
        _currentCardSide.value = false
    }

    fun nextCard() {
        _studyPosition.value = _studyPosition.value?.plus(1)
        _currentCardSide.value = false
    }

    fun updateSelectedTab(position: Int) {
        _selectedTab.value = position
    }

    fun resetSelectedTab() {
        _selectedTab.value = 0
    }

    fun updateSelectedDeckPosition(position: Int) {
        _selectedDeckPosition.value = position
    }

    fun updateSelectedCardPosition(position: Int?) {
        _selectedCardPosition.value = position
    }

}