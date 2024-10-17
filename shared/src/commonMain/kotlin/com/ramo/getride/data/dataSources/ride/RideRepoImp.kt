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
import com.ramo.getride.global.util.loggerError
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

class RideRepoImp(supabase: Supabase) : BaseRepoImp(supabase), RideRepo {

    override suspend fun getRideById(rideId: Long, invoke: (Ride?) -> Unit) {
        querySingleRealTime(SUPA_RIDE, channelName = "public:$SUPA_RIDE", primaryKey = Ride::id, block =  {
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

    override suspend fun getActiveRideForUser(userId: Long, invoke: (Ride?) -> Unit) {
        querySingleRealTime(SUPA_RIDE, channelName = "public:$SUPA_RIDE", primaryKey = Ride::id, block =  {
            and {
                Ride::userId eq userId
                or {
                    Ride::status eq -1
                    Ride::status eq 0
                    Ride::status eq 1
                    Ride::status eq 2
                    Ride::status eq 3
                }
            }
        }, invoke = invoke)
    }

    override suspend fun cancelRideRealTime() {
        cancelSingleRealTime("public:$SUPA_RIDE")
    }

    override suspend fun getActiveRideForDriver(driverId: Long, invoke: (Ride?) -> Unit) {
        querySingleRealTime(SUPA_RIDE, channelName = "public:$SUPA_RIDE", primaryKey = Ride::id, block =  {
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

    override suspend fun editDriverLocation(rideId: Long, driverLocation: Location): Int = try {
        buildJsonObject {
            put("item_id", rideId)
            put("driver_location", Json.encodeToJsonElement(driverLocation))
        }.let {
            rpc("edit_ride_driver_location", it)
        }
    } catch (e: Exception) {
        loggerError(error = e.stackTraceToString())
        -2
    }

    override suspend fun deleteRide(id: Long): Int = delete(SUPA_RIDE, id)

    override suspend fun getNearRideInsertsDeletes(
        currentLocation: Location,
        onInsert: (RideRequest) -> Unit,
        onChanged: (RideRequest) -> Unit,
        onDelete: (Long) -> Unit
    ) {
        kotlinx.coroutines.coroutineScope {
            realTimeQueryInsertsDeletes<RideRequest>(realTable = SUPA_RIDE_REQUEST, { record ->
                record.applyFilterNearRideInserts(currentLocation) {
                    onInsert(it)
                }
            }, changed = { record ->
                record.applyFilterNearRideInserts(currentLocation) {
                    onChanged(it)
                }
            }) {
                onDelete(it)
            }
        }
    }

    override suspend fun getNearRideRequestsForDriver(
        currentLocation: Location,
        invoke: (List<RideRequest>) -> Unit,
    ) {
        val lat = currentLocation.latitude - AREA_RIDE_FIRST_PHASE to currentLocation.latitude + AREA_RIDE_FIRST_PHASE
        val lng = currentLocation.longitude - AREA_RIDE_FIRST_PHASE to currentLocation.longitude + AREA_RIDE_FIRST_PHASE
        query<RideRequest>(SUPA_RIDE_REQUEST) {
            and {
                filter("from->latitude", FilterOperator.GTE, lat.first)
                filter("from->latitude", FilterOperator.LTE, lat.second)
                filter("from->longitude", FilterOperator.GTE, lng.first)
                filter("from->longitude", FilterOperator.LTE, lng.second)
                //filter("date", FilterOperator.GTE, dateBeforeHour) // @OmAr-Kader => Remove that comment
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

    override suspend fun getRideRequestById(rideRequestId: Long, invoke: (RideRequest?) -> Unit) {
        querySingleRealTime(SUPA_RIDE_REQUEST,channelName = "public:$SUPA_RIDE_REQUEST", primaryKey = RideRequest::id, block =  {
            RideRequest::id eq rideRequestId
        }, invoke = invoke)
    }

    override suspend fun cancelRideRequestRealTime() {
        cancelSingleRealTime("public:$SUPA_RIDE_REQUEST")
    }

    override suspend fun addNewRideRequest(item: RideRequest): RideRequest? = insert(SUPA_RIDE_REQUEST, item)

    override suspend fun editRideRequest(item: RideRequest): RideRequest? = edit(SUPA_RIDE_REQUEST, item.id, item)

    override suspend fun editAddDriverProposal(rideRequestId: Long, rideProposal: RideProposal): Int = try {
        buildJsonObject {
            put("item_id", rideRequestId)
            put("new_proposal", Json.encodeToJsonElement(rideProposal))
        }.let {
            rpc("append_proposal_to_requests", it)
        }
    } catch (e: Exception) {
        loggerError(error = e.stackTraceToString())
        -2
    }

    override suspend fun editRemoveDriverProposal(rideRequestId: Long, proposalToRemove: RideProposal): Int = try {
        buildJsonObject {
            put("item_id", rideRequestId)
            put("driver_id", proposalToRemove.driverId)
        }.let {
            rpc("remove_proposal_from_requests", it)
        }
    } catch (e: Exception) {
        loggerError(error = e.stackTraceToString())
        -2
    }

    override suspend fun deleteRideRequest(id: Long): Int = delete(SUPA_RIDE_REQUEST, id)
}