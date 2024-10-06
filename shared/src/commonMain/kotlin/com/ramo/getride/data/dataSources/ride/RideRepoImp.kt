package com.ramo.getride.data.dataSources.ride

import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideProposal
import com.ramo.getride.data.model.RideRequest
import com.ramo.getride.data.util.BaseRepoImp
import com.ramo.getride.data.util.applyFilterNearRideInserts
import com.ramo.getride.data.util.applyFilterNearRideRequests
import com.ramo.getride.global.base.AREA_RIDE_FIRST_PHASE
import com.ramo.getride.global.base.SUPA_RIDE
import com.ramo.getride.global.base.SUPA_RIDE_REQUEST
import com.ramo.getride.global.base.Supabase
import com.ramo.getride.global.util.dateBeforeHour
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

    override suspend fun getActiveRideForUser(userId: Long, invoke: suspend (Ride?) -> Unit) {
        querySingleRealTime(SUPA_RIDE, primaryKey = Ride::id, block =  {
            and {
                Ride::userId eq userId
                or {
                    Ride::status eq -1
                    Ride::status eq 0
                    Ride::status eq 1
                    Ride::status eq 2
                }
            }
        }, invoke = invoke)
    }

    override suspend fun getActiveRideForDriver(driverId: Long, invoke: suspend (Ride?) -> Unit) {
        querySingleRealTime(SUPA_RIDE, primaryKey = Ride::id, block =  {
            and {
                Ride::driverId eq driverId
                or {
                    Ride::status eq 0
                    Ride::status eq 1
                    Ride::status eq 2
                    Ride::status eq 3
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
            and {
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

    override suspend fun getNearRideInserts(
        currentLocation: Location,
        insert: suspend (RideRequest) -> Unit,
    ) {
        kotlinx.coroutines.coroutineScope {
            realTimeQueryInserts<RideRequest>(realTable = SUPA_RIDE_REQUEST) { record ->
                record.applyFilterNearRideInserts(currentLocation) {
                    insert(it)
                }
            }
        }
    }

    override suspend fun getNearRideRequestsForDriver(
        currentLocation: Location,
        invoke: suspend (List<RideRequest>) -> Unit,
    ) {
        val lat = currentLocation.latitude - AREA_RIDE_FIRST_PHASE to currentLocation.latitude + AREA_RIDE_FIRST_PHASE
        val lng = currentLocation.longitude - AREA_RIDE_FIRST_PHASE to currentLocation.longitude + AREA_RIDE_FIRST_PHASE
        query<RideRequest>(SUPA_RIDE_REQUEST) {
            and {
                filter("from->latitude", FilterOperator.GTE, lat.first)
                filter("from->latitude", FilterOperator.LTE, lat.second)
                filter("from->longitude", FilterOperator.GTE, lng.first)
                filter("from->longitude", FilterOperator.LTE, lng.second)
                filter("date", FilterOperator.GTE, dateBeforeHour)
                filter("chosen_one", FilterOperator.EQ, 0)
            }
        }.map { it.id }.also { ids ->
            queryRealTime(
                table = SUPA_RIDE_REQUEST,
                primaryKey = RideRequest::id,
                filter = FilterOperation("id", FilterOperator.IN, "(${ids.joinToString(",")})"),
            ) { requests ->
                invoke(requests.applyFilterNearRideRequests(currentLocation))
            }
        }
    }

    override suspend fun getRideRequestById(rideRequestId: Long, invoke: suspend (RideRequest?) -> Unit) {
        querySingleRealTime(SUPA_RIDE_REQUEST, primaryKey = RideRequest::id, block =  {
            RideRequest::id eq rideRequestId
        }, invoke = invoke)
    }

    override suspend fun addNewRideRequest(item: RideRequest): RideRequest? = insert(SUPA_RIDE_REQUEST, item)

    override suspend fun editRideRequest(item: RideRequest): RideRequest? = edit(SUPA_RIDE_REQUEST, item.id, item)

    override suspend fun editAddDriverProposal(rideRequestId: Long, rideProposal: RideProposal): Int = try {
        buildJsonObject {
            put("item_id", rideRequestId) // First parameter (TEXT)
            put("new_proposal", Json.encodeToJsonElement(rideProposal)) // Second parameter (Object as JSON)
        }.let {
            rpc("append_to_requests", it)
        }
    } catch (e: Exception) {
        loggerError(error = e.stackTraceToString())
        -2
    }

    override suspend fun editRemoveDriverProposal(rideRequestId: Long, driverId: Long): Int = try {
        buildJsonObject {
            put("item_id", rideRequestId) // First parameter
            put("driver_id", driverId) // Second parameter
        }.let {
            rpc("remove_from_requests", it)
        }
    } catch (e: Exception) {
        loggerError(error = e.stackTraceToString())
        -2
    }

    override suspend fun deleteRideRequest(id: Long): Int = delete(SUPA_RIDE_REQUEST, id)
}