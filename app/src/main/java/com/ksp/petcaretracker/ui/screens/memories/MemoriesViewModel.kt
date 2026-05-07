package com.ksp.petcaretracker.ui.screens.memories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.data.repository.MemoryRepository
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.domain.model.Memory
import com.ksp.petcaretracker.domain.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemoriesUiState(val pet: Pet? = null, val memories: List<Memory> = emptyList(), val isLoading: Boolean = true, val selectedMemory: Memory? = null)

@HiltViewModel
class MemoriesViewModel @Inject constructor(private val memoryRepository: MemoryRepository, private val petRepository: PetRepository, savedStateHandle: SavedStateHandle) : ViewModel() {
    val petId: Long = savedStateHandle.get<String>("petId")?.toLongOrNull() ?: -1
    private val _uiState = MutableStateFlow(MemoriesUiState())
    val uiState: StateFlow<MemoriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { petRepository.getPetById(petId).collect { pet -> _uiState.update { it.copy(pet = pet) } } }
        viewModelScope.launch { memoryRepository.getMemoriesForPet(petId).collect { list -> _uiState.update { it.copy(memories = list, isLoading = false) } } }
    }

    fun addMemory(imageUri: String, caption: String = "") { viewModelScope.launch { memoryRepository.upsertMemory(Memory(petId = petId, imageUri = imageUri, caption = caption)) } }
    fun deleteMemory(id: Long) { viewModelScope.launch { memoryRepository.deleteMemory(id) } }
    fun selectMemory(memory: Memory?) { _uiState.update { it.copy(selectedMemory = memory) } }
}
