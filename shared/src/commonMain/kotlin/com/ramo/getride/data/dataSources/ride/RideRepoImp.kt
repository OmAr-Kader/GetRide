package com.ramo.getride.data.dataSources.ride

import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideRequest
import com.ramo.getride.data.util.BaseRepoImp
import com.ramo.getride.global.base.AREA_RIDE_FIRST_PHASE
import com.ramo.getride.global.base.SUPA_RIDE
import com.ramo.getride.global.base.SUPA_RIDE_REQUEST
import com.ramo.getride.global.base.Supabase
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator

class RideRepoImp(supabase: Supabase) : BaseRepoImp(supabase), RideRepo {

    override suspend fun getRideById(rideId: Long, invoke: suspend (Ride) -> Unit) {
        querySingleRealTime(SUPA_RIDE_REQUEST, primaryKey = Ride::id, block =  {
            Ride::id eq rideId
        }, invoke = invoke)
    }

    override suspend fun addNewRide(item: Ride): Ride? = insert(SUPA_RIDE, item)

    override suspend fun editRide(item: Ride): Ride? = edit(SUPA_RIDE, item.id, item)

    override suspend fun deleteRide(id: Long): Int = delete(SUPA_RIDE, id)

    /*override suspend fun getNearRideRequestsForDriver(location: Location, invoke: suspend (List<RideRequest>) -> Unit) {
        val lat = location.latitude - AREA_RIDE_FIRST_PHASE to location.latitude + AREA_RIDE_FIRST_PHASE
        val long = location.longitude - AREA_RIDE_FIRST_PHASE to location.longitude + AREA_RIDE_FIRST_PHASE
        query<RideRequest>(SUPA_RIDE_REQUEST) {
            or {
                rangeGte("from->latitude", lat)
                rangeGte("from->longitude", long)
            }
        }.also { invoke(it) }.map { it.id }.also { requestIds ->
            queryRealTime(
                table = SUPA_RIDE_REQUEST,
                primaryKey = RideRequest::id,
                filter = FilterOperation("id", FilterOperator.IN, "(${requestIds.joinToString(",")})"),
                invoke = invoke
            )
        }
    }*/

    override suspend fun getNearRideRequestsForDriver(location: Location, invoke: suspend (List<RideRequest>) -> Unit) {
        val lat = location.latitude - AREA_RIDE_FIRST_PHASE to location.latitude + AREA_RIDE_FIRST_PHASE
        val long = location.longitude - AREA_RIDE_FIRST_PHASE to location.longitude + AREA_RIDE_FIRST_PHASE
        queryRealTime(
            table = SUPA_RIDE_REQUEST,
            primaryKey = RideRequest::id,
            filter = FilterOperation("from->latitude", FilterOperator.NXL, "(${lat.first},${lat.second})"),
        ) { requests ->
            requests.filter {
                it.from.longitude >= long.first && it.from.longitude <= long.second
            }.also { invoke(it) }
        }
    }

    override suspend fun addNewRideRequest(item: RideRequest): RideRequest? = insert(SUPA_RIDE_REQUEST, item)

    override suspend fun editRideRequest(item: RideRequest): RideRequest? = edit(SUPA_RIDE_REQUEST, item.id, item)

    override suspend fun deleteRideRequest(id: Long): Int = delete(SUPA_RIDE_REQUEST, id)
}