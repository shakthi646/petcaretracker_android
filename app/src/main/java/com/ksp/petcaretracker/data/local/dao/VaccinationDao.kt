package com.ksp.petcaretracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ksp.petcaretracker.data.local.entity.VaccinationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccinationDao {

    @Query("SELECT * FROM vaccinations WHERE petId = :petId ORDER BY nextDueDate ASC")
    fun getVaccinationsForPet(petId: Long): Flow<List<VaccinationEntity>>

    @Query("SELECT * FROM vaccinations WHERE id = :id")
    fun getVaccinationById(id: Long): Flow<VaccinationEntity?>

    @Query("SELECT * FROM vaccinations WHERE id = :id")
    suspend fun getVaccinationByIdOnce(id: Long): VaccinationEntity?

    @Upsert
    suspend fun upsertVaccination(vaccination: VaccinationEntity): Long

    @Query("DELETE FROM vaccinations WHERE id = :id")
    suspend fun deleteVaccination(id: Long)

    @Query("SELECT * FROM vaccinations WHERE nextDueDate >= :fromMillis ORDER BY nextDueDate ASC")
    fun getUpcomingVaccinations(fromMillis: Long): Flow<List<VaccinationEntity>>
}
