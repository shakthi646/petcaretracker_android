package com.ksp.petcaretracker.ui.screens.vet

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.PetCareApp
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.data.repository.VetVisitRepository
import com.ksp.petcaretracker.domain.model.VetVisit
import com.ksp.petcaretracker.utils.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditVetVisitState(val clinicName: String = "", val doctorName: String = "", val date: Long = System.currentTimeMillis(), val reminderEnabled: Boolean = false, val prescriptionImageUri: String? = null, val notes: String = "", val isEditing: Boolean = false, val isSaved: Boolean = false, val error: String? = null)

@HiltViewModel
class AddEditVetVisitViewModel @Inject constructor(
    private val vetVisitRepository: VetVisitRepository, private val petRepository: PetRepository, private val application: Application, savedStateHandle: SavedStateHandle
) : ViewModel() {
    val petId: Long = savedStateHandle.get<String>("petId")?.toLongOrNull() ?: -1
    private val vetVisitId: Long? = savedStateHandle.get<String>("vetVisitId")?.toLongOrNull()
    private val _state = MutableStateFlow(AddEditVetVisitState())
    val state: StateFlow<AddEditVetVisitState> = _state.asStateFlow()

    init { vetVisitId?.let { loadVetVisit(it) } }

    private fun loadVetVisit(id: Long) { viewModelScope.launch { vetVisitRepository.getVetVisitById(id).collect { visit -> visit?.let { v -> _state.update { it.copy(clinicName = v.clinicName, doctorName = v.doctorName, date = v.date, reminderEnabled = v.reminderEnabled, prescriptionImageUri = v.prescriptionImageUri, notes = v.notes, isEditing = true) } } } } }

    fun onClinicNameChange(name: String) { _state.update { it.copy(clinicName = name) } }
    fun onDoctorNameChange(name: String) { _state.update { it.copy(doctorName = name) } }
    fun onDateChange(date: Long) { _state.update { it.copy(date = date) } }
    fun onReminderToggle(enabled: Boolean) { _state.update { it.copy(reminderEnabled = enabled) } }
    fun onPrescriptionImageChange(uri: String?) { _state.update { it.copy(prescriptionImageUri = uri) } }
    fun onNotesChange(notes: String) { _state.update { it.copy(notes = notes) } }

    fun save() {
        val s = _state.value
        if (s.clinicName.isBlank()) { _state.update { it.copy(error = "Clinic name is required") }; return }
        viewModelScope.launch {
            val visit = VetVisit(id = vetVisitId ?: 0, petId = petId, clinicName = s.clinicName.trim(), doctorName = s.doctorName.trim(), date = s.date, reminderEnabled = s.reminderEnabled, prescriptionImageUri = s.prescriptionImageUri, notes = s.notes.trim())
            val savedId = vetVisitRepository.upsertVetVisit(visit)
            if (s.reminderEnabled) { val petName = petRepository.getPetByIdOnce(petId)?.name ?: "Your pet"; ReminderScheduler.scheduleReminder(context = application, uniqueWorkName = "vet_visit_$savedId", title = "Vet Appointment Reminder", message = "$petName has a vet appointment at ${s.clinicName} tomorrow!", channelId = PetCareApp.CHANNEL_VET_APPOINTMENT, triggerAtMillis = s.date - 86400000, notificationId = (savedId + 10000).toInt()) }
            else { ReminderScheduler.cancelReminder(application, "vet_visit_${vetVisitId ?: savedId}") }
            _state.update { it.copy(isSaved = true) }
        }
    }
}
