package com.ramo.getride.data.model

import com.ramo.getride.data.util.BaseObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class Ride(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("user_id")
    val userId: Long = 0,
    @SerialName("driver_id")
    val driverId: Long = 0L,
    @SerialName("from")
    val from: Location = Location(),
    @SerialName("to")
    val to: Location = Location(),
    @SerialName("current_driver")
    val currentDriver: Location = Location(),
    @SerialName("fare")
    val fare: Int = 0,
    @SerialName("status")
    val status: Int = 0, // Driver on Way = 0, Driver Cancel = -1, User Cancel = -2, Driver Arrived = 1, Here We Go = 2
): BaseObject() {

    constructor() : this(0L, 0L, 0L, Location(), Location(), Location(), 0, 0)

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}

@Serializable
data class Location(
    @SerialName("latitude")
    val latitude: Double = 0.0,
    @SerialName("longitude")
    val longitude: Double = 0.0,
): BaseObject() {

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}

@Serializable
data class RideRequest(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("user_id")
    val userId: Long = 0,
    @SerialName("from")
    val from: Location = Location(),
    @SerialName("to")
    val to: Location = Location(),
    @SerialName("duration_distance")
    val durationDistance: String = "",
    @SerialName("fare")
    val fare: Int = 0,
    @SerialName("drivers")
    val drivers: List<RideProposal> = listOf(),
    @SerialName("chosen_one")
    val chosenDriver: Long = 0,
    @SerialName("chosen_ride")
    val chosenDriverRide: Long = 0,
): BaseObject() {

    constructor() : this(0L, 0L, Location(), Location(), "", 0, listOf())

    fun isDriverChosen(driverId: Long): Boolean = driverId == chosenDriver

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}

@Serializable
data class RideProposal(
    @SerialName("driver_id")
    val driverId: Long = 0,
    @SerialName("fare")
    val fare: Int = 0,
): BaseObject() {
    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}

