package com.ksp.petcaretracker.ui.screens.pet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.domain.model.Gender
import com.ksp.petcaretracker.domain.model.Pet
import com.ksp.petcaretracker.domain.model.PetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditPetState(
    val name: String = "",
    val type: PetType = PetType.DOG,
    val breed: String = "",
    val dateOfBirth: Long = System.currentTimeMillis(),
    val weight: String = "",
    val gender: Gender = Gender.MALE,
    val imageUri: String? = null,
    val isEditing: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddEditPetViewModel @Inject constructor(
    private val petRepository: PetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val petId: Long? = savedStateHandle.get<String>("petId")?.toLongOrNull()

    private val _state = MutableStateFlow(AddEditPetState())
    val state: StateFlow<AddEditPetState> = _state.asStateFlow()

    init { petId?.let { loadPet(it) } }

    private fun loadPet(id: Long) {
        viewModelScope.launch {
            petRepository.getPetByIdOnce(id)?.let { pet ->
                _state.update {
                    it.copy(name = pet.name, type = pet.type, breed = pet.breed, dateOfBirth = pet.dateOfBirth, weight = pet.weight.toString(), gender = pet.gender, imageUri = pet.imageUri, isEditing = true)
                }
            }
        }
    }

    fun onNameChange(name: String) { _state.update { it.copy(name = name) } }
    fun onTypeChange(type: PetType) { _state.update { it.copy(type = type) } }
    fun onBreedChange(breed: String) { _state.update { it.copy(breed = breed) } }
    fun onDateChange(date: Long) { _state.update { it.copy(dateOfBirth = date) } }
    fun onWeightChange(weight: String) { _state.update { it.copy(weight = weight) } }
    fun onGenderChange(gender: Gender) { _state.update { it.copy(gender = gender) } }
    fun onImageUriChange(uri: String?) { _state.update { it.copy(imageUri = uri) } }

    fun savePet() {
        val currentState = _state.value
        if (currentState.name.isBlank()) { _state.update { it.copy(error = "Name is required") }; return }
        viewModelScope.launch {
            val pet = Pet(id = petId ?: 0, name = currentState.name.trim(), type = currentState.type, breed = currentState.breed.trim(), dateOfBirth = currentState.dateOfBirth, weight = currentState.weight.toFloatOrNull() ?: 0f, gender = currentState.gender, imageUri = currentState.imageUri)
            petRepository.upsertPet(pet)
            _state.update { it.copy(isSaved = true) }
        }
    }
}
