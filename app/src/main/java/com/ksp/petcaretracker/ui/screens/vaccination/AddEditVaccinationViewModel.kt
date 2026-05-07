package com.ksp.petcaretracker.ui.screens.vaccination

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.PetCareApp
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.data.repository.VaccinationRepository
import com.ksp.petcaretracker.domain.model.Vaccination
import com.ksp.petcaretracker.utils.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditVaccinationState(
    val vaccineName: String = "",
    val lastDate: Long = System.currentTimeMillis(),
    val nextDueDate: Long = System.currentTimeMillis(),
    val reminderEnabled: Boolean = false,
    val notes: String = "",
    val isEditing: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddEditVaccinationViewModel @Inject constructor(
    private val vaccinationRepository: VaccinationRepository,
    private val petRepository: PetRepository,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val petId: Long = savedStateHandle.get<String>("petId")?.toLongOrNull() ?: -1
    private val vaccinationId: Long? = savedStateHandle.get<String>("vaccinationId")?.toLongOrNull()

    private val _state = MutableStateFlow(AddEditVaccinationState())
    val state: StateFlow<AddEditVaccinationState> = _state.asStateFlow()

    init { vaccinationId?.let { loadVaccination(it) } }

    private fun loadVaccination(id: Long) {
        viewModelScope.launch {
            vaccinationRepository.getVaccinationByIdOnce(id)?.let { v ->
                _state.update { it.copy(vaccineName = v.vaccineName, lastDate = v.lastDate, nextDueDate = v.nextDueDate, reminderEnabled = v.reminderEnabled, notes = v.notes, isEditing = true) }
            }
        }
    }

    fun onVaccineNameChange(name: String) { _state.update { it.copy(vaccineName = name) } }
    fun onLastDateChange(date: Long) { _state.update { it.copy(lastDate = date) } }
    fun onNextDueDateChange(date: Long) { _state.update { it.copy(nextDueDate = date) } }
    fun onReminderToggle(enabled: Boolean) { _state.update { it.copy(reminderEnabled = enabled) } }
    fun onNotesChange(notes: String) { _state.update { it.copy(notes = notes) } }

    fun save() {
        val s = _state.value
        if (s.vaccineName.isBlank()) { _state.update { it.copy(error = "Vaccine name is required") }; return }
        viewModelScope.launch {
            val vaccination = Vaccination(id = vaccinationId ?: 0, petId = petId, vaccineName = s.vaccineName.trim(), lastDate = s.lastDate, nextDueDate = s.nextDueDate, reminderEnabled = s.reminderEnabled, notes = s.notes.trim())
            val savedId = vaccinationRepository.upsertVaccination(vaccination)
            if (s.reminderEnabled) {
                val petName = petRepository.getPetByIdOnce(petId)?.name ?: "Your pet"
                ReminderScheduler.scheduleReminder(context = application, uniqueWorkName = "vaccination_$savedId", title = "Vaccination Reminder", message = "$petName's ${s.vaccineName} vaccination is due tomorrow!", channelId = PetCareApp.CHANNEL_VACCINATION, triggerAtMillis = s.nextDueDate - 86400000, notificationId = savedId.toInt())
            } else { ReminderScheduler.cancelReminder(application, "vaccination_${vaccinationId ?: savedId}") }
            _state.update { it.copy(isSaved = true) }
        }
    }
}
