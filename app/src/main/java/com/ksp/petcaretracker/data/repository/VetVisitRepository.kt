package com.ksp.petcaretracker.data.repository

import com.ksp.petcaretracker.data.local.dao.VetVisitDao
import com.ksp.petcaretracker.domain.model.VetVisit
import com.ksp.petcaretracker.domain.model.toDomain
import com.ksp.petcaretracker.domain.model.toEntity
import com.ksp.petcaretracker.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VetVisitRepository @Inject constructor(
    private val vetVisitDao: VetVisitDao
) {
    fun getVetVisitsForPet(petId: Long): Flow<List<VetVisit>> =
        vetVisitDao.getVetVisitsForPet(petId).map { list -> list.map { it.toDomain() } }

    fun getVetVisitById(id: Long): Flow<VetVisit?> =
        vetVisitDao.getVetVisitById(id).map { it?.toDomain() }

    suspend fun upsertVetVisit(vetVisit: VetVisit): Long =
        vetVisitDao.upsertVetVisit(vetVisit.toEntity())

    suspend fun deleteVetVisit(id: Long) =
        vetVisitDao.deleteVetVisit(id)

    fun getUpcomingVisits(): Flow<List<VetVisit>> =
        vetVisitDao.getUpcomingVisits(DateUtils.todayStartMillis())
            .map { list -> list.map { it.toDomain() } }
}
