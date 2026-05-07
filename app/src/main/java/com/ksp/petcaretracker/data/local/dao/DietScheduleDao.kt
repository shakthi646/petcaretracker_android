package com.ksp.petcaretracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ksp.petcaretracker.data.local.entity.DietScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DietScheduleDao {

    @Query("SELECT * FROM diet_schedules WHERE petId = :petId ORDER BY CASE mealTime WHEN 'Morning' THEN 1 WHEN 'Afternoon' THEN 2 WHEN 'Evening' THEN 3 ELSE 4 END")
    fun getDietSchedulesForPet(petId: Long): Flow<List<DietScheduleEntity>>

    @Query("SELECT * FROM diet_schedules WHERE id = :id")
    fun getDietScheduleById(id: Long): Flow<DietScheduleEntity?>

    @Upsert
    suspend fun upsertDietSchedule(dietSchedule: DietScheduleEntity): Long

    @Query("DELETE FROM diet_schedules WHERE id = :id")
    suspend fun deleteDietSchedule(id: Long)
}
