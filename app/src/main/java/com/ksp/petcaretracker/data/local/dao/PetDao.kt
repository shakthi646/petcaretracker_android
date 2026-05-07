package com.ksp.petcaretracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ksp.petcaretracker.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    @Query("SELECT * FROM pets ORDER BY createdAt DESC")
    fun getAllPets(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE id = :petId")
    fun getPetById(petId: Long): Flow<PetEntity?>

    @Query("SELECT * FROM pets WHERE id = :petId")
    suspend fun getPetByIdOnce(petId: Long): PetEntity?

    @Upsert
    suspend fun upsertPet(pet: PetEntity): Long

    @Query("DELETE FROM pets WHERE id = :petId")
    suspend fun deletePet(petId: Long)

    @Query("SELECT COUNT(*) FROM pets")
    fun getPetCount(): Flow<Int>
}
