package com.ksp.petcaretracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "Dog" or "Cat"
    val breed: String,
    val dateOfBirth: Long, // epoch millis
    val weight: Float,
    val gender: String, // "Male" or "Female"
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
