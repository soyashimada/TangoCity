package nl.hva.tangocity.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ReviewViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedTab: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTab : LiveData<Int>
        get() = _selectedTab

    private val _selectedDeckPosition: MutableLiveData<Int> = MutableLiveData(0)
    val selectedDeckPosition : LiveData<Int>
        get() = _selectedDeckPosition

    private val _selectedCardPosition: MutableLiveData<Int?> = MutableLiveData()
    val selectedCardPosition : LiveData<Int?>
        get() = _selectedCardPosition

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