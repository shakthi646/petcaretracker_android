package com.ksp.petcaretracker.ui.screens.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.domain.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PetListUiState {
    data object Loading : PetListUiState
    data class Success(val pets: List<Pet>) : PetListUiState
    data class Error(val message: String) : PetListUiState
}

@HiltViewModel
class PetListViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    val uiState: StateFlow<PetListUiState> = petRepository.getAllPets()
        .map<List<Pet>, PetListUiState> { PetListUiState.Success(it) }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = PetListUiState.Loading)

    fun deletePet(petId: Long) {
        viewModelScope.launch { petRepository.deletePet(petId) }
    }
}
