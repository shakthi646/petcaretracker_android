package com.ksp.petcaretracker.di

import android.content.Context
import androidx.room.Room
import com.ksp.petcaretracker.data.local.PetCareDatabase
import com.ksp.petcaretracker.data.local.dao.DietScheduleDao
import com.ksp.petcaretracker.data.local.dao.GrowthLogDao
import com.ksp.petcaretracker.data.local.dao.MemoryDao
import com.ksp.petcaretracker.data.local.dao.PetDao
import com.ksp.petcaretracker.data.local.dao.VaccinationDao
import com.ksp.petcaretracker.data.local.dao.VetVisitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PetCareDatabase {
        return Room.databaseBuilder(
            context,
            PetCareDatabase::class.java,
            PetCareDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun providePetDao(database: PetCareDatabase): PetDao = database.petDao()

    @Provides
    fun provideVaccinationDao(database: PetCareDatabase): VaccinationDao = database.vaccinationDao()

    @Provides
    fun provideVetVisitDao(database: PetCareDatabase): VetVisitDao = database.vetVisitDao()

    @Provides
    fun provideDietScheduleDao(database: PetCareDatabase): DietScheduleDao = database.dietScheduleDao()

    @Provides
    fun provideGrowthLogDao(database: PetCareDatabase): GrowthLogDao = database.growthLogDao()

    @Provides
    fun provideMemoryDao(database: PetCareDatabase): MemoryDao = database.memoryDao()
}
