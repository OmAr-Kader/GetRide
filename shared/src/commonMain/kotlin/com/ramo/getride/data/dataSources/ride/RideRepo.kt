package com.ramo.getride.data.dataSources.ride

import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideProposal
import com.ramo.getride.data.model.RideRequest

interface RideRepo {

    suspend fun getRideById(rideId: Long, invoke: suspend (Ride?) -> Unit) // From Both
    suspend fun getAllRidesForUser(userId: Long): List<Ride> // From User
    suspend fun getAllRidesForDriver(driverId: Long): List<Ride> // From Driver
    suspend fun getActiveRidesForUser(userId: Long, invoke: suspend (Ride?) -> Unit) // From User
    suspend fun getActiveRidesForDriver(driverId: Long, invoke: suspend (Ride?) -> Unit) // From Driver
    suspend fun addNewRide(item: Ride): Ride? // From User
    suspend fun editRide(item: Ride): Ride? // From Driver
    suspend fun deleteRide(id: Long): Int

    suspend fun getNearRideRequestsForDriver(location: Location, invoke: suspend (List<RideRequest>) -> Unit) // From Driver
    suspend fun getRideRequestById(rideRequestId: Long, invoke: suspend (RideRequest?) -> Unit) // // From User
    suspend fun addNewRideRequest(item: RideRequest): RideRequest? // From User
    suspend fun editRideRequest(item: RideRequest): RideRequest? // From User
    suspend fun editAddDriverProposal(rideId: Long, rideProposal: RideProposal): Int // From Driver
    suspend fun deleteRideRequest(id: Long): Int // From Driver

}