package com.ramo.getride.data.dataSources.ride

import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideProposal
import com.ramo.getride.data.model.RideRequest

@Suppress("unused")
class RideBase(
    private val repo: RideRepo
) {

    suspend fun getRideById(rideId: Long, invoke: (Ride?) -> Unit) = repo.getRideById(rideId, invoke)
    suspend fun getAllRidesForUser(userId: Long): List<Ride> = repo.getAllRidesForUser(userId = userId)
    suspend fun getAllRidesForDriver(driverId: Long): List<Ride> = repo.getAllRidesForDriver(driverId = driverId)
    suspend fun getActiveRideForUser(userId: Long, invoke: (Ride?) -> Unit) = repo.getActiveRideForUser(userId = userId, invoke)
    suspend fun getActiveRideForDriver(driverId: Long, invoke: (Ride?) -> Unit) = repo.getActiveRideForDriver(driverId = driverId, invoke)
    suspend fun cancelRideRealTime() = repo.cancelRideRealTime()
    suspend fun addNewRide(item: Ride): Ride? = repo.addNewRide(item)
    suspend fun editRide(item: Ride): Ride? = repo.editRide(item)
    suspend fun editDriverLocation(rideId: Long, driverLocation: Location): Int = repo.editDriverLocation(rideId, driverLocation)
    suspend fun deleteRide(id: Long): Int = repo.deleteRide(id)

    suspend fun getNearRideInsertsDeletes(
        currentLocation: Location,
        onInsert: (RideRequest) -> Unit,
        onChanged: (RideRequest) -> Unit,
        onDelete: (Long) -> Unit
    ) = repo.getNearRideInsertsDeletes(currentLocation, onInsert, onChanged, onDelete)
    suspend fun getNearRideRequestsForDriver(
        currentLocation: Location,
        invoke: (List<RideRequest>) -> Unit
    ) = repo.getNearRideRequestsForDriver(currentLocation, invoke)
    suspend fun getRideRequestById(rideRequestId: Long, invoke: (RideRequest?) -> Unit) = repo.getRideRequestById(rideRequestId, invoke)
    suspend fun cancelRideRequestRealTime() = repo.cancelRideRequestRealTime()
    suspend fun addNewRideRequest(item: RideRequest): RideRequest? = repo.addNewRideRequest(item)
    suspend fun editRideRequest(item: RideRequest): RideRequest? = repo.editRideRequest(item)
    suspend fun editAddDriverProposal(rideRequestId: Long, rideProposal: RideProposal): Int = repo.editAddDriverProposal(rideRequestId, rideProposal)
    suspend fun editRemoveDriverProposal(
        rideRequestId: Long,
        proposalToRemove: RideProposal
    ): Int = repo.editRemoveDriverProposal(rideRequestId, proposalToRemove)
    suspend fun deleteRideRequest(id: Long): Int = repo.deleteRideRequest(id)

}