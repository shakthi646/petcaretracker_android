package com.ksp.petcaretracker.ui.screens.diet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.data.repository.DietScheduleRepository
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.domain.model.DietSchedule
import com.ksp.petcaretracker.domain.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DietScheduleListUiState(val pet: Pet? = null, val schedules: List<DietSchedule> = emptyList(), val isLoading: Boolean = true)

@HiltViewModel
class DietScheduleViewModel @Inject constructor(private val dietScheduleRepository: DietScheduleRepository, private val petRepository: PetRepository, savedStateHandle: SavedStateHandle) : ViewModel() {
    val petId: Long = savedStateHandle.get<String>("petId")?.toLongOrNull() ?: -1
    private val _uiState = MutableStateFlow(DietScheduleListUiState())
    val uiState: StateFlow<DietScheduleListUiState> = _uiState.asStateFlow()
    init {
        viewModelScope.launch { petRepository.getPetById(petId).collect { pet -> _uiState.update { it.copy(pet = pet) } } }
        viewModelScope.launch { dietScheduleRepository.getDietSchedulesForPet(petId).collect { list -> _uiState.update { it.copy(schedules = list, isLoading = false) } } }
    }
    fun deleteDietSchedule(id: Long) { viewModelScope.launch { dietScheduleRepository.deleteDietSchedule(id) } }
}
