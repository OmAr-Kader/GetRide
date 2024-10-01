package com.ramo.getride.data.dataSources.ride

import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideProposal
import com.ramo.getride.data.model.RideRequest
import com.ramo.getride.data.util.BaseRepoImp
import com.ramo.getride.global.base.AREA_RIDE_FIRST_PHASE
import com.ramo.getride.global.base.SUPA_RIDE
import com.ramo.getride.global.base.SUPA_RIDE_REQUEST
import com.ramo.getride.global.base.Supabase
import com.ramo.getride.global.util.loggerError
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

class RideRepoImp(supabase: Supabase) : BaseRepoImp(supabase), RideRepo {

    override suspend fun getRideById(rideId: Long, invoke: suspend (Ride?) -> Unit) {
        querySingleRealTime(SUPA_RIDE, primaryKey = Ride::id, block =  {
            Ride::id eq rideId
        }, invoke = invoke)
    }

    override suspend fun getAllRidesForUser(userId: Long): List<Ride> = query(SUPA_RIDE) {
        and {
            Ride::userId eq userId
            Ride::status neq 0
            Ride::status neq 1
            Ride::status neq 2
        }
    }

    override suspend fun getAllRidesForDriver(driverId: Long): List<Ride> = query(SUPA_RIDE) {
        and {
            Ride::driverId eq driverId
            Ride::status neq 0
            Ride::status neq 1
            Ride::status neq 2
        }
    }

    override suspend fun getActiveRidesForUser(userId: Long, invoke: suspend (Ride?) -> Unit) {
        querySingle<Ride>(SUPA_RIDE) {
            and {
                Ride::userId eq userId
                or {
                    Ride::status eq 0
                    Ride::status eq 1
                    Ride::status eq 2
                }
            }
        }
        querySingleRealTime(SUPA_RIDE, primaryKey = Ride::id, block =  {
            and {
                Ride::userId eq userId
                or {
                    Ride::status eq 0
                    Ride::status eq 1
                    Ride::status eq 2
                }
            }
        }, invoke = invoke)
    }

    override suspend fun getActiveRidesForDriver(driverId: Long, invoke: suspend (Ride?) -> Unit) {
        querySingleRealTime(SUPA_RIDE, primaryKey = Ride::id, block =  {
            and {
                Ride::driverId eq driverId
                or {
                    Ride::status eq 0
                    Ride::status eq 1
                    Ride::status eq 2
                }
            }
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

    override suspend fun getRideRequestById(rideRequestId: Long, invoke: suspend (RideRequest?) -> Unit) {
        querySingleRealTime(SUPA_RIDE_REQUEST, primaryKey = RideRequest::id, block =  {
            RideRequest::id eq rideRequestId
        }, invoke = invoke)
    }

    override suspend fun addNewRideRequest(item: RideRequest): RideRequest? = insert(SUPA_RIDE_REQUEST, item)

    override suspend fun editRideRequest(item: RideRequest): RideRequest? = edit(SUPA_RIDE_REQUEST, item.id, item)

    override suspend fun editAddDriverProposal(rideId: Long, rideProposal: RideProposal): Int = try {
        buildJsonObject {
            put("item_id", rideId) // First parameter (TEXT)
            put("new_proposal", Json.encodeToJsonElement(rideProposal)) // Second parameter (Object as JSON)
        }.let {
            rpc("append_to_requests", it)
        }
    } catch (e: Exception) {
        loggerError(error = e.stackTraceToString())
        -2
    }

    override suspend fun deleteRideRequest(id: Long): Int = delete(SUPA_RIDE_REQUEST, id)
}