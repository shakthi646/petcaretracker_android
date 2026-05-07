package com.ksp.petcaretracker.ui.screens.growth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.data.repository.GrowthLogRepository
import com.ksp.petcaretracker.domain.model.GrowthLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddGrowthLogState(val weight: String = "", val date: Long = System.currentTimeMillis(), val notes: String = "", val isSaved: Boolean = false, val error: String? = null)

@HiltViewModel
class AddGrowthLogViewModel @Inject constructor(private val growthLogRepository: GrowthLogRepository, savedStateHandle: SavedStateHandle) : ViewModel() {
    val petId: Long = savedStateHandle.get<String>("petId")?.toLongOrNull() ?: -1
    private val _state = MutableStateFlow(AddGrowthLogState())
    val state: StateFlow<AddGrowthLogState> = _state.asStateFlow()

    fun onWeightChange(weight: String) { _state.update { it.copy(weight = weight) } }
    fun onDateChange(date: Long) { _state.update { it.copy(date = date) } }
    fun onNotesChange(notes: String) { _state.update { it.copy(notes = notes) } }

    fun save() {
        val s = _state.value; val weight = s.weight.toFloatOrNull()
        if (weight == null || weight <= 0) { _state.update { it.copy(error = "Enter a valid weight") }; return }
        viewModelScope.launch { growthLogRepository.upsertGrowthLog(GrowthLog(petId = petId, weight = weight, date = s.date, notes = s.notes.trim())); _state.update { it.copy(isSaved = true) } }
    }
}
