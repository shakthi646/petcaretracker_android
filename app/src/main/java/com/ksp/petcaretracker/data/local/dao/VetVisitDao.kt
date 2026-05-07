package com.ksp.petcaretracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ksp.petcaretracker.data.local.entity.VetVisitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VetVisitDao {

    @Query("SELECT * FROM vet_visits WHERE petId = :petId ORDER BY date DESC")
    fun getVetVisitsForPet(petId: Long): Flow<List<VetVisitEntity>>

    @Query("SELECT * FROM vet_visits WHERE id = :id")
    fun getVetVisitById(id: Long): Flow<VetVisitEntity?>

    @Upsert
    suspend fun upsertVetVisit(vetVisit: VetVisitEntity): Long

    @Query("DELETE FROM vet_visits WHERE id = :id")
    suspend fun deleteVetVisit(id: Long)

    @Query("SELECT * FROM vet_visits WHERE date >= :fromMillis ORDER BY date ASC")
    fun getUpcomingVisits(fromMillis: Long): Flow<List<VetVisitEntity>>
}
