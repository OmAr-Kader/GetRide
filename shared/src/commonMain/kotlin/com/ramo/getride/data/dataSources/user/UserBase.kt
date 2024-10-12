package com.ramo.getride.data.dataSources.user

import com.ramo.getride.data.model.User
import com.ramo.getride.data.model.UserData
import com.ramo.getride.data.model.UserRate

class UserBase(
    private val repo: UserRepo
) {
    suspend fun getUserOnAuthId(authId: String): User? = repo.getUserOnAuthId(authId)
    suspend fun getUserOnEmail(email: String): User? = repo.getUserOnEmail(email)
    suspend fun getAllUsersOnAuthIds(ids: List<String>): List<User> = repo.getAllUsersOnAuthIds(ids)
    suspend fun getUserDetails(id: Long, invoke: suspend (UserData?) -> Unit) = repo.getUserDetails(id, invoke)
    suspend fun addNewUser(item: User): User? = repo.addNewUser(item)
    suspend fun editUser(item: User): User? = repo.editUser(item)
    suspend fun deleteUser(id: Long): Int = repo.deleteUser(id)

    suspend fun addNewUserRate(item: UserRate): UserRate? = repo.addNewUserRate(item)
    suspend fun addEditUserRate(userId: Long, rate: Float): Int = repo.addEditUserRate(userId, rate)
    suspend fun editUserRate(item: UserRate): UserRate? = repo.editUserRate(item)
    suspend fun deleteUserRate(id: Long): Int = repo.deleteUserRate(id)
}