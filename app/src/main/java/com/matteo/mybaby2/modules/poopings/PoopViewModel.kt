package com.matteo.mybaby2.modules.poopings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matteo.mybaby2.modules.poopings.repositories.IPoopingRepository
import com.matteo.mybaby2.modules.poopings.schemas.PoopingRead
import com.matteo.mybaby2.modules.poopings.schemas.PoopingUpsert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

sealed class PoopUiState {
    object Loading : PoopUiState()
    object Success : PoopUiState()
    data class Error(val exception: Throwable) : PoopUiState()
}

class PoopViewModel(private val repository: IPoopingRepository) : ViewModel() {
    var id = mutableStateOf<Int?>(null)
        private set
    var hasPoop = mutableStateOf(false)
        private set
    var hasPiss = mutableStateOf(false)
        private set
    var notes = mutableStateOf("")
        private set
    var date = mutableStateOf<Long?>(null)
        private set
    var uiState = mutableStateOf<PoopUiState>(PoopUiState.Loading)
        private set
    var poopings = mutableStateOf<List<PoopingRead>>(emptyList())

    fun updateHasPoop(hasPoop: Boolean) {
        this.hasPoop.value = hasPoop
    }

    fun updateHasPiss(hasPiss: Boolean) {
        this.hasPiss.value = hasPiss
    }

    fun updateDate(date: Long?) {
        this.date.value = date
    }

    fun updateNotes(notes: String) {
        this.notes.value = notes
    }

    fun submit() {
        uiState.value = PoopUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (date.value == null) {
                    // TODO dirty hack, add validations!
                    date.value = System.currentTimeMillis()
                }

                repository.upsertPooping(
                    PoopingUpsert(
                        id.value,
                        hasPoop.value,
                        hasPiss.value,
                        notes.value,
                        date.value!!
                    )
                )
                uiState.value = PoopUiState.Success
            } catch (exception: Exception) {
                uiState.value = PoopUiState.Error(exception)
            }
        }
    }

    fun getAllPoopingsByDate(date: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val localeDate =
                    Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
                val result = repository.getAllByDate(localeDate)
                poopings.value = result
            } catch (exception: Exception) {
                // TODO error handling
            }
        }
    }

    fun deletePooping(pooping: PoopingRead) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                uiState.value = PoopUiState.Loading
                repository.deletePooping(pooping)
                uiState.value = PoopUiState.Success
            } catch (exception: Exception) {
                // TODO error handling
                uiState.value = PoopUiState.Error(exception)
            }
        }
    }

    fun patchForm(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                uiState.value = PoopUiState.Loading
                val value = repository.getById(id)
                if (value != null) {
                    this@PoopViewModel.id.value = value.id
                    hasPoop.value = value.hasPoop
                    hasPiss.value = value.hasPiss
                    notes.value = value.notes
                    date.value = value.date
                }
            } catch (exception: Exception) {
                uiState.value = PoopUiState.Error(exception)
            }
        }
    }

}