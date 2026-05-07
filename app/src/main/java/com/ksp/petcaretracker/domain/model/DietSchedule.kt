package com.ksp.petcaretracker.domain.model

data class DietSchedule(
    val id: Long = 0,
    val petId: Long = 0,
    val mealTime: MealTime = MealTime.MORNING,
    val foodType: String = "",
    val quantity: String = "",
    val reminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MealTime(val displayName: String) {
    MORNING("Morning"),
    AFTERNOON("Afternoon"),
    EVENING("Evening")
}
