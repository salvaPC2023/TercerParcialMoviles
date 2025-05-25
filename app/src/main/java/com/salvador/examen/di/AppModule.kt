package com.salvador.examen.di

import com.salvador.data.PlanRepository
import com.salvador.data.plan.IPlanDataSource
import com.salvador.usecases.GetPlans
import com.salvador.framework.plan.PlanDataSource


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ppModule {

    @Provides
    @Singleton
    fun providePlanDataSource(): IPlanDataSource {
        return PlanDataSource()
    }

    @Provides
    @Singleton
    fun providePlanRepository(dataSource: IPlanDataSource): PlanRepository {
        return PlanRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideGetPlans(planRepository: PlanRepository): GetPlans {
        return GetPlans(planRepository)
    }

}