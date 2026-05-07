package com.ksp.petcaretracker.ui.screens.diet

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksp.petcaretracker.PetCareApp
import com.ksp.petcaretracker.data.repository.DietScheduleRepository
import com.ksp.petcaretracker.data.repository.PetRepository
import com.ksp.petcaretracker.domain.model.DietSchedule
import com.ksp.petcaretracker.domain.model.MealTime
import com.ksp.petcaretracker.utils.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class AddEditDietState(val mealTime: MealTime = MealTime.MORNING, val foodType: String = "", val quantity: String = "", val reminderEnabled: Boolean = false, val isEditing: Boolean = false, val isSaved: Boolean = false, val error: String? = null)

@HiltViewModel
class AddEditDietViewModel @Inject constructor(private val dietScheduleRepository: DietScheduleRepository, private val petRepository: PetRepository, private val application: Application, savedStateHandle: SavedStateHandle) : ViewModel() {
    val petId: Long = savedStateHandle.get<String>("petId")?.toLongOrNull() ?: -1
    private val dietId: Long? = savedStateHandle.get<String>("dietId")?.toLongOrNull()
    private val _state = MutableStateFlow(AddEditDietState())
    val state: StateFlow<AddEditDietState> = _state.asStateFlow()

    init { dietId?.let { loadDiet(it) } }

    private fun loadDiet(id: Long) { viewModelScope.launch { dietScheduleRepository.getDietScheduleById(id).collect { diet -> diet?.let { d -> _state.update { it.copy(mealTime = d.mealTime, foodType = d.foodType, quantity = d.quantity, reminderEnabled = d.reminderEnabled, isEditing = true) } } } } }

    fun onMealTimeChange(mealTime: MealTime) { _state.update { it.copy(mealTime = mealTime) } }
    fun onFoodTypeChange(foodType: String) { _state.update { it.copy(foodType = foodType) } }
    fun onQuantityChange(quantity: String) { _state.update { it.copy(quantity = quantity) } }
    fun onReminderToggle(enabled: Boolean) { _state.update { it.copy(reminderEnabled = enabled) } }

    fun save() {
        val s = _state.value
        if (s.foodType.isBlank()) { _state.update { it.copy(error = "Food type is required") }; return }
        viewModelScope.launch {
            val schedule = DietSchedule(id = dietId ?: 0, petId = petId, mealTime = s.mealTime, foodType = s.foodType.trim(), quantity = s.quantity.trim(), reminderEnabled = s.reminderEnabled)
            val savedId = dietScheduleRepository.upsertDietSchedule(schedule)
            if (s.reminderEnabled) {
                val petName = petRepository.getPetByIdOnce(petId)?.name ?: "Your pet"
                val hour = when (s.mealTime) { MealTime.MORNING -> 8; MealTime.AFTERNOON -> 13; MealTime.EVENING -> 19 }
                val triggerTime = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
                ReminderScheduler.scheduleReminder(context = application, uniqueWorkName = "diet_$savedId", title = "Feeding Time!", message = "Time to feed $petName - ${s.mealTime.displayName}: ${s.foodType}", channelId = PetCareApp.CHANNEL_DIET, triggerAtMillis = triggerTime, notificationId = (savedId + 20000).toInt())
            } else { ReminderScheduler.cancelReminder(application, "diet_${dietId ?: savedId}") }
            _state.update { it.copy(isSaved = true) }
        }
    }
}
