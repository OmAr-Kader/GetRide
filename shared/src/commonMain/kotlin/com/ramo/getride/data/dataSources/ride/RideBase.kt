package com.ramo.getride.data.dataSources.ride

import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideProposal
import com.ramo.getride.data.model.RideRequest

class RideBase(
    private val repo: RideRepo
) {

    suspend fun getRideById(rideId: Long, invoke: suspend (Ride?) -> Unit) = repo.getRideById(rideId, invoke)
    suspend fun getAllRidesForUser(userId: Long): List<Ride> = repo.getAllRidesForUser(userId = userId)
    suspend fun getAllRidesForDriver(driverId: Long): List<Ride> = repo.getAllRidesForDriver(driverId = driverId)
    suspend fun getActiveRidesForUser(userId: Long, invoke: suspend (Ride?) -> Unit) = repo.getActiveRidesForUser(userId = userId, invoke)
    suspend fun getActiveRidesForDriver(driverId: Long, invoke: suspend (Ride?) -> Unit) = repo.getActiveRidesForDriver(driverId = driverId, invoke)
    suspend fun addNewRide(item: Ride): Ride? = repo.addNewRide(item)
    suspend fun editRide(item: Ride): Ride? = repo.editRide(item)
    suspend fun deleteRide(id: Long): Int = repo.deleteRide(id)

    suspend fun getNearRideRequestsForDriver(
        location: Location,
        invoke: suspend (List<RideRequest>) -> Unit
    ) = repo.getNearRideRequestsForDriver(location, invoke)
    suspend fun getRideRequestById(rideRequestId: Long, invoke: suspend (RideRequest?) -> Unit) = repo.getRideRequestById(rideRequestId, invoke)
    suspend fun addNewRideRequest(item: RideRequest): RideRequest? = repo.addNewRideRequest(item)
    suspend fun editRideRequest(item: RideRequest): RideRequest? = repo.editRideRequest(item)
    suspend fun editAddDriverProposal(rideId: Long, rideProposal: RideProposal): Int = repo.editAddDriverProposal(rideId, rideProposal)
    suspend fun deleteRideRequest(id: Long): Int = repo.deleteRideRequest(id)
}