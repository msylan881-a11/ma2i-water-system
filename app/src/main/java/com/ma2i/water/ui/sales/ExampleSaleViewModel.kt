package com.ma2i.water.ui.sales

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma2i.water.data.model.Sale
import com.ma2i.water.data.repository.SaleRepository
import com.ma2i.water.util.safesave.SafeSaveHelper

/**
 * Example ViewModel showing how to call SafeSaveHelper from a ViewModel (MVVM).
 * The ViewModel exposes a LiveData<SaveState> that the Activity observes to update the UI.
 */
class ExampleSaleViewModel(private val repository: SaleRepository) : ViewModel() {

    sealed class SaveState {
        object Idle : SaveState()
        object Saving : SaveState()
        object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }

    private val _saveState = MutableLiveData<SaveState>(SaveState.Idle)
    val saveState: LiveData<SaveState> = _saveState

    fun saveSale(sale: Sale) {
        _saveState.value = SaveState.Saving
        // Use SafeSaveHelper safely inside ViewModelScope.
        SafeSaveHelper.safeSave(
            scope = viewModelScope,
            dbSave = {
                // Delegate to repository; this should be a suspend function that writes to DB
                repository.saveSale(sale)
            },
            fileSave = null, // optional: e.g., backupManager.maybeAutoBackup()
            onSuccess = {
                // Post success state
                _saveState.postValue(SaveState.Success)
            },
            onError = { throwable ->
                _saveState.postValue(SaveState.Error(throwable.message ?: "حدث خطأ أثناء الحفظ"))
            }
        )
    }

    fun resetState() {
        _saveState.value = SaveState.Idle
    }
}
