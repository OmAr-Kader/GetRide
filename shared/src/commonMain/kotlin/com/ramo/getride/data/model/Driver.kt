package com.ramo.getride.data.model

import com.ramo.getride.data.util.BaseObject
import com.ramo.getride.global.base.SUPA_DRIVER_LICENCE
import com.ramo.getride.global.base.SUPA_DRIVER_RATE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class Driver(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("driver_id")
    val driverId: String = "",
    @SerialName("email")
    val email: String = "",
    @SerialName("driver_name")
    val driverName: String = "",
    @SerialName("driver_car")
    val driverCar: String = "",
    @SerialName("driver_car_number")
    val driverCarNumber: String = "",
    @SerialName("driver_car_color")
    val driverCarColor: String = "",
    @SerialName("profile_picture")
    val profilePicture: String = ""
): BaseObject() {

    constructor() : this(0L, "",  "", "", "", "","","")

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}

@Serializable
data class DriverRate(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("driver_id")
    val driverId: String = "",
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
data class DriverLicences(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("driver_id")
    val driverId: String = "",
    @SerialName("driver_gov_id")
    val driverGovID: String = "",
    @SerialName("driver_licence")
    val driverLicence: String = "",
    @SerialName("car_licence")
    val carLicence: String = ""
): BaseObject() {

    constructor() : this(0L, "",  "", "")

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}

@Serializable
data class DriverDetails(
    @SerialName(SUPA_DRIVER_RATE) val driverRate: DriverRate = DriverRate(),
)

@Serializable
data class DriverAllDetails(
    @SerialName(SUPA_DRIVER_RATE) val driverRate: DriverRate = DriverRate(),
    @SerialName(SUPA_DRIVER_LICENCE) val requests: DriverLicences = DriverLicences()
)
