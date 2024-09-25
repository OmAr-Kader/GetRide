package com.ramo.getride.data.dataSources.profile

import com.ramo.getride.data.model.User
import com.ramo.getride.data.util.BaseRepoImp
import com.ramo.getride.global.base.SUPA_USER
import com.ramo.getride.global.base.Supabase

class ProfileRepoImp(supabase: Supabase) : BaseRepoImp(supabase), ProfileRepo {

    override suspend fun getProfileOnUserId(userId: String): User? = querySingle(SUPA_USER) {
        User::userId eq userId
    }

    override suspend fun getProfileOnEmail(email: String): User? = querySingle(SUPA_USER) {
        User::email eq email
    }

    override suspend fun getProfileOnUsername(username: String): User? = querySingle(SUPA_USER) {
        User::username eq username
    }

    override suspend fun getAllProfilesOnUserIds(ids: List<String>): List<User> = query(SUPA_USER) {
        User::userId isIn ids
    }

    override suspend fun fetchProfilesOnName(name: String): List<User> = query(SUPA_USER) {
        or {
            User::name ilike "%${name}%"
            User::username ilike "%${name}%"
        }
    }

    override suspend fun addNewUser(item: User): User? = insert(SUPA_USER, item)

    override suspend fun editUser(item: User): User? = edit(SUPA_USER, item.id, item)

    override suspend fun deleteUser(id: Long): Int = delete(SUPA_USER, id)
}

