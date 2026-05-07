package com.ksp.petcaretracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vaccinations",
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
data class VaccinationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val vaccineName: String,
    val lastDate: Long,
    val nextDueDate: Long,
    val reminderEnabled: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
