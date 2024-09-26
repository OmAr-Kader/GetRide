package com.ramo.getride.data.dataSources.user

import com.ramo.getride.data.model.User
import com.ramo.getride.data.model.UserData
import com.ramo.getride.data.model.UserRate

interface UserRepo {

    suspend fun getUserOnAuthId(authId: String): User?
    suspend fun getUserOnEmail(email: String): User?
    suspend fun getAllUsersOnAuthIds(ids: List<String>): List<User>
    suspend fun getUserDetails(id: Long, invoke: suspend (UserData?) -> Unit)
    suspend fun addNewUser(item: User): User?
    suspend fun editUser(item: User): User?
    suspend fun deleteUser(id: Long): Int

    suspend fun addNewUserRate(item: UserRate): UserRate?
    suspend fun editUserRate(item: UserRate): UserRate?
    suspend fun deleteUserRate(id: Long): Int
}