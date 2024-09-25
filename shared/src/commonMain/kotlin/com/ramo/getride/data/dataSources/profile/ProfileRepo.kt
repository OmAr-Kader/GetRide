package com.ramo.getride.data.dataSources.profile

import com.ramo.getride.data.model.User

interface ProfileRepo {

    suspend fun getProfileOnUserId(userId: String): User?
    suspend fun getProfileOnEmail(email: String): User?
    suspend fun getProfileOnUsername(username: String): User?
    suspend fun getAllProfilesOnUserIds(ids: List<String>): List<User>
    suspend fun fetchProfilesOnName(name: String): List<User>
    suspend fun addNewUser(item: User): User?
    suspend fun editUser(item: User): User?
    suspend fun deleteUser(id: Long): Int
}