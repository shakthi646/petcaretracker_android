package com.ksp.petcaretracker.data.repository

import com.ksp.petcaretracker.data.local.dao.DietScheduleDao
import com.ksp.petcaretracker.domain.model.DietSchedule
import com.ksp.petcaretracker.domain.model.toDomain
import com.ksp.petcaretracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DietScheduleRepository @Inject constructor(
    private val dietScheduleDao: DietScheduleDao
) {
    fun getDietSchedulesForPet(petId: Long): Flow<List<DietSchedule>> =
        dietScheduleDao.getDietSchedulesForPet(petId).map { list -> list.map { it.toDomain() } }

    fun getDietScheduleById(id: Long): Flow<DietSchedule?> =
        dietScheduleDao.getDietScheduleById(id).map { it?.toDomain() }

    suspend fun upsertDietSchedule(dietSchedule: DietSchedule): Long =
        dietScheduleDao.upsertDietSchedule(dietSchedule.toEntity())

    suspend fun deleteDietSchedule(id: Long) =
        dietScheduleDao.deleteDietSchedule(id)
}
