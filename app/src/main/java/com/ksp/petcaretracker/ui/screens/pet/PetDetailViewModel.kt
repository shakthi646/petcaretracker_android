package com.ksp.petcaretracker.ui.screens.pet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.domain.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PetDetailUiState(
    val pet: Pet? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class PetDetailViewModel @Inject constructor(
    private val petRepository: PetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val petId: Long = savedStateHandle.get<String>("petId")?.toLongOrNull() ?: -1

    private val _uiState = MutableStateFlow(PetDetailUiState())
    val uiState: StateFlow<PetDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            petRepository.getPetById(petId).collect { pet ->
                _uiState.update { it.copy(pet = pet, isLoading = false) }
            }
        }
    }

    fun deletePet(id: Long) {
        viewModelScope.launch {
            petRepository.deletePet(id)
        }
    }
}
