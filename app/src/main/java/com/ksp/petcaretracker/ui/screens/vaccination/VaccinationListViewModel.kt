package com.ksp.petcaretracker.ui.screens.vaccination

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.data.repository.VaccinationRepository
import com.ksp.petcaretracker.domain.model.Pet
import com.ksp.petcaretracker.domain.model.Vaccination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VaccinationListUiState(
    val pet: Pet? = null,
    val vaccinations: List<Vaccination> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class VaccinationListViewModel @Inject constructor(
    private val vaccinationRepository: VaccinationRepository,
    private val petRepository: PetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val petId: Long = savedStateHandle.get<String>("petId")?.toLongOrNull() ?: -1

    private val _uiState = MutableStateFlow(VaccinationListUiState())
    val uiState: StateFlow<VaccinationListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            petRepository.getPetById(petId).collect { pet ->
                _uiState.update { it.copy(pet = pet) }
            }
        }
        viewModelScope.launch {
            vaccinationRepository.getVaccinationsForPet(petId).collect { list ->
                _uiState.update { it.copy(vaccinations = list, isLoading = false) }
            }
        }
    }

    fun deleteVaccination(id: Long) {
        viewModelScope.launch { vaccinationRepository.deleteVaccination(id) }
    }
}
