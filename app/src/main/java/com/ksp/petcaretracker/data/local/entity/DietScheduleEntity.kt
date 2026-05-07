package com.ksp.petcaretracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diet_schedules",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class DietScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val mealTime: String, // "Morning", "Afternoon", "Evening"
    val foodType: String,
    val quantity: String,
    val reminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
