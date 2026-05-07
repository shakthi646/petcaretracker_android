package com.ksp.petcaretracker.ui.screens.growth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.data.repository.GrowthLogRepository
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.domain.model.GrowthLog
import com.ksp.petcaretracker.domain.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GrowthLogUiState(val pet: Pet? = null, val logs: List<GrowthLog> = emptyList(), val isLoading: Boolean = true)

@HiltViewModel
class GrowthLogViewModel @Inject constructor(private val growthLogRepository: GrowthLogRepository, private val petRepository: PetRepository, savedStateHandle: SavedStateHandle) : ViewModel() {
    val petId: Long = savedStateHandle.get<String>("petId")?.toLongOrNull() ?: -1
    private val _uiState = MutableStateFlow(GrowthLogUiState())
    val uiState: StateFlow<GrowthLogUiState> = _uiState.asStateFlow()
    init {
        viewModelScope.launch { petRepository.getPetById(petId).collect { pet -> _uiState.update { it.copy(pet = pet) } } }
        viewModelScope.launch { growthLogRepository.getGrowthLogsForPet(petId).collect { list -> _uiState.update { it.copy(logs = list, isLoading = false) } } }
    }
    fun deleteGrowthLog(id: Long) { viewModelScope.launch { growthLogRepository.deleteGrowthLog(id) } }
}
