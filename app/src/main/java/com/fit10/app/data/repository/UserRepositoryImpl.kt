package com.fit10.app.data.repository

import com.fit10.app.data.db.dao.UserProfileDao
import com.fit10.app.data.db.mapper.toDomain
import com.fit10.app.data.db.mapper.toEntity
import com.fit10.app.data.preferences.UserPreferencesDataSource
import com.fit10.app.domain.model.UserProfile
import com.fit10.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userProfileDao: UserProfileDao,
    private val userPreferences: UserPreferencesDataSource
) : UserRepository {

    override fun getUserProfile(): Flow<UserProfile?> =
        userProfileDao.getUserProfile().map { it?.toDomain() }

    override suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdate(profile.toEntity())
        userPreferences.setActiveUserId(profile.id)
    }

    override fun isOnboardingCompleted(): Flow<Boolean> =
        userPreferences.isOnboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) =
        userPreferences.setOnboardingCompleted(completed)
}
