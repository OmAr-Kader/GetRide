package com.ramo.getride.data.dataSources.user

import com.ramo.getride.data.model.User
import com.ramo.getride.data.model.UserData
import com.ramo.getride.data.model.UserDetails
import com.ramo.getride.data.model.UserRate
import com.ramo.getride.data.util.BaseRepoImp
import com.ramo.getride.data.util.toListOfObject
import com.ramo.getride.global.base.SUPA_USER
import com.ramo.getride.global.base.SUPA_USER_RATE
import com.ramo.getride.global.base.Supabase
import io.github.jan.supabase.postgrest.query.Columns

class UserRepoImp(supabase: Supabase) : BaseRepoImp(supabase), UserRepo {

    override suspend fun getUserOnAuthId(authId: String): User? = querySingle(SUPA_USER) {
        User::authId eq authId
    }

    override suspend fun getUserOnEmail(email: String): User? = querySingle(SUPA_USER) {
        User::email eq email
    }

    override suspend fun getAllUsersOnAuthIds(ids: List<String>): List<User> = query(SUPA_USER) {
        User::authId isIn ids
    }

    override suspend fun getUserDetails(id: Long, invoke: suspend (UserData?) -> Unit) {
        queryWithForeign(SUPA_USER, Columns.raw("*, $SUPA_USER_RATE(*)")) {
            User::id eq id
        }?.apply {
            toListOfObject<UserDetails>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })?.firstOrNull().let { result ->
                toListOfObject<User>(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                })?.firstOrNull().let { if (it == User()) null else it }?.let { user ->
                    result?.userRate?.let { if (it == UserRate()) null else result.userRate }?.let { userRate ->
                        invoke(UserData(user, userRate))
                    } ?: invoke(null)
                }
            }

        } ?: invoke(null)
    }

    override suspend fun addNewUser(item: User): User? = insert(SUPA_USER, item)

    override suspend fun editUser(item: User): User? = edit(SUPA_USER, item.id, item)

    override suspend fun deleteUser(id: Long): Int = delete(SUPA_USER, id)


    override suspend fun addNewUserRate(item: UserRate): UserRate? = insert(SUPA_USER_RATE, item)

    override suspend fun editUserRate(item: UserRate): UserRate? = edit(SUPA_USER_RATE, item.id, item)

    override suspend fun deleteUserRate(id: Long): Int = delete(SUPA_USER_RATE, id)
}

