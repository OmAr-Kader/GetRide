package com.ramo.getride.data.dataSources.ride

import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideRequest

interface RideRepo {

    suspend fun getRideById(rideId: Long, invoke: suspend (Ride) -> Unit) // From Both
    suspend fun addNewRide(item: Ride): Ride? // From User
    suspend fun editRide(item: Ride): Ride? // From Driver
    suspend fun deleteRide(id: Long): Int

    suspend fun getNearRideRequestsForDriver(location: Location, invoke: suspend (List<RideRequest>) -> Unit) // From Driver
    suspend fun addNewRideRequest(item: RideRequest): RideRequest? // From User
    suspend fun editRideRequest(item: RideRequest): RideRequest? // From Driver
    suspend fun deleteRideRequest(id: Long): Int // From Driver

}