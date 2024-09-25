package com.ramo.getride.data.model

import com.ramo.getride.data.util.BaseObject
import com.ramo.getride.global.base.SUPA_USER_RATE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class User(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("username")
    val username: String = "",
    @SerialName("email")
    val email: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("bio")
    val bio: String = "",
    @SerialName("profile_picture")
    val profilePicture: String = "",
    @Transient
    val mode: Int = 0, // Addable = 0, Cancelable = -1, Acceptable = -2, Not Addable = 1, Own = 2
): BaseObject() {

    constructor() : this(0L, "", "", "", "", "", "", 0)

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}


@Serializable
data class UserRate(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("rate")
    val rate: Float = 5F,
    @SerialName("raters")
    val raters: List<Long> = listOf()
): BaseObject() {

    val rateStr: String
        get() {
            return rate.toString()
        }

    constructor() : this(0L, "",  5F, listOf())

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}


@Serializable
data class UserDetails(
    @SerialName(SUPA_USER_RATE) val userRate: UserRate = UserRate(),
)

@Serializable
data class UserBase(
    @SerialName("id")
    val id: String = "",
    @SerialName("username")
    val username: String = "",
    @SerialName("email")
    val email: String = "",
    @Transient
    val name: String = "",
    @Transient
    val profilePicture: String = ""
) {
    constructor() : this("", "", "", "", "")

}