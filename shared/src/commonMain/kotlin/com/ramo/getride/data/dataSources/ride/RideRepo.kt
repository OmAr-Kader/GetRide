package com.ramo.getride.data.dataSources.ride

import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideProposal
import com.ramo.getride.data.model.RideRequest

interface RideRepo {

    suspend fun getRideById(rideId: Long, invoke: (Ride?) -> Unit) // From Both
    suspend fun getAllRidesForUser(userId: Long): List<Ride> // From User
    suspend fun getAllRidesForDriver(driverId: Long): List<Ride> // From Driver
    suspend fun getActiveRideForUser(userId: Long, invoke: (Ride?) -> Unit) // From User
    suspend fun getActiveRideForDriver(driverId: Long, invoke: (Ride?) -> Unit) // From Driver
    suspend fun addNewRide(item: Ride): Ride? // From User
    suspend fun editRide(item: Ride): Ride? // From Driver
    suspend fun editDriverLocation(rideId: Long, driverLocation: Location): Int // From Driver
    suspend fun deleteRide(id: Long): Int

    suspend fun getNearRideInsertsDeletes(currentLocation: Location, onInsert: (RideRequest) -> Unit, onDelete: (Long) -> Unit) // From Driver
    suspend fun getNearRideRequestsForDriver(currentLocation: Location, invoke: (List<RideRequest>) -> Unit, ) // From Driver
    suspend fun getRideRequestById(rideRequestId: Long, invoke: (RideRequest?) -> Unit) // // From User
    suspend fun addNewRideRequest(item: RideRequest): RideRequest? // From User
    suspend fun editRideRequest(item: RideRequest): RideRequest? // From User
    suspend fun editAddDriverProposal(rideRequestId: Long, rideProposal: RideProposal): Int // From Driver
    suspend fun editRemoveDriverProposal(rideRequestId: Long, proposalToRemove: RideProposal): Int // From Driver
    suspend fun deleteRideRequest(id: Long): Int // From Driver

}