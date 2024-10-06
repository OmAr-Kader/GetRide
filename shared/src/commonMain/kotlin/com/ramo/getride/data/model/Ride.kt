@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.ramo.getride.data.model

import com.ramo.getride.data.util.BaseObject
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
    val fare: Double = 0.0,
    @SerialName("status")
    val status: Int = 0, // Driver Not Moved = 0, Driver on Way = 1, Driver Arrived = 2, Here We Go = 3, Ride Done = 4, Driver Cancel = -1, User Cancel = -2,
    @SerialName("date")
    val date: String = "",
    @SerialName("duration_distance")
    val durationDistance: String = "",
    @SerialName("driver_name")
    val driverName: String = "",
): BaseObject() {

    val dateInstant: Instant get() = Instant.parse(date)

    val dateStr: String
        get() = dateInstant.toLocalDateTime(TimeZone.currentSystemDefault()).toString()

    private val dateTime: LocalDateTime get() = dateInstant.toLocalDateTime(TimeZone.currentSystemDefault())

    val timestamp: String get() {
        return dateTime.format(LocalDateTime.Format {
            byUnicodePattern("MM/dd HH:mm")
        })
    }

    constructor() : this(0L, 0L, 0L, Location(), Location(), Location(), 0.0, 0, "")

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
    @SerialName("title")
    val title: String = "",
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
    val fare: Double = 0.0,
    @SerialName("drivers")
    val driverProposals: List<RideProposal> = listOf(),
    @SerialName("chosen_one")
    val chosenDriver: Long = 0,
    @SerialName("chosen_ride")
    val chosenRide: Long = 0,
    @SerialName("date")
    val date: String = "",
    @Transient
    val isDriverCanSubmit: Boolean = true, // For Driver,
    @Transient
    val requestHadSubmit: Boolean = false, // For Driver
): BaseObject() {

    val dateInstant: Instant get() = Instant.parse(date)

    val dateStr: String
        get() = dateInstant.toLocalDateTime(TimeZone.currentSystemDefault()).toString()

    val dateTime: LocalDateTime get() = dateInstant.toLocalDateTime(TimeZone.currentSystemDefault())

    val timestamp: String get() {
        return dateTime.format(LocalDateTime.Format {
            byUnicodePattern("MM/dd HH:mm")
        })
    }

    constructor() : this(0L, 0L, Location(), Location(), "", 0.0, listOf())

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
    @SerialName("driver_name")
    val driverName: String = "",
    @SerialName("rate")
    val rate: Float = 5F,
    @SerialName("fare")
    val fare: Double = 0.9,
    @SerialName("current_driver")
    val currentDriver: Location = Location(),
): BaseObject() {
    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}

