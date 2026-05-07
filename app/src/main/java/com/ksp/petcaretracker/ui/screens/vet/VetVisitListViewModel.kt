package com.ksp.petcaretracker.ui.screens.vet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.data.repository.VetVisitRepository
import com.ksp.petcaretracker.domain.model.Pet
import com.ksp.petcaretracker.domain.model.VetVisit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VetVisitListUiState(val pet: Pet? = null, val vetVisits: List<VetVisit> = emptyList(), val isLoading: Boolean = true)

@HiltViewModel
class VetVisitListViewModel @Inject constructor(
    private val vetVisitRepository: VetVisitRepository,
    private val petRepository: PetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val petId: Long = savedStateHandle.get<String>("petId")?.toLongOrNull() ?: -1
    private val _uiState = MutableStateFlow(VetVisitListUiState())
    val uiState: StateFlow<VetVisitListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { petRepository.getPetById(petId).collect { pet -> _uiState.update { it.copy(pet = pet) } } }
        viewModelScope.launch { vetVisitRepository.getVetVisitsForPet(petId).collect { list -> _uiState.update { it.copy(vetVisits = list, isLoading = false) } } }
    }

    fun deleteVetVisit(id: Long) { viewModelScope.launch { vetVisitRepository.deleteVetVisit(id) } }
}
