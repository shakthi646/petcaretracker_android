package com.ksp.petcaretracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.data.repository.VaccinationRepository
import com.ksp.petcaretracker.data.repository.VetVisitRepository
import com.ksp.petcaretracker.domain.model.Pet
import com.ksp.petcaretracker.domain.model.Vaccination
import com.ksp.petcaretracker.domain.model.VetVisit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val pets: List<Pet> = emptyList(),
    val upcomingVaccinations: List<Vaccination> = emptyList(),
    val upcomingVetVisits: List<VetVisit> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    petRepository: PetRepository,
    vaccinationRepository: VaccinationRepository,
    vetVisitRepository: VetVisitRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        petRepository.getAllPets(),
        vaccinationRepository.getUpcomingVaccinations(),
        vetVisitRepository.getUpcomingVisits()
    ) { pets, vaccinations, vetVisits ->
        HomeUiState(
            pets = pets,
            upcomingVaccinations = vaccinations.take(5),
            upcomingVetVisits = vetVisits.take(5),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
