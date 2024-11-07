package com.ramo.getride.data.dataSources.driver

import com.ramo.getride.data.model.Driver
import com.ramo.getride.data.model.DriverAdminData
import com.ramo.getride.data.model.DriverAllDetails
import com.ramo.getride.data.model.DriverData
import com.ramo.getride.data.model.DriverDetails
import com.ramo.getride.data.model.DriverLicences
import com.ramo.getride.data.model.DriverRate
import com.ramo.getride.data.util.BaseRepoImp
import com.ramo.getride.data.util.toListOfObject
import com.ramo.getride.global.base.SUPA_DRIVER
import com.ramo.getride.global.base.SUPA_DRIVER_RATE
import com.ramo.getride.global.base.SUPA_DRIVER_LICENCE
import com.ramo.getride.global.base.Supabase
import com.ramo.getride.global.util.loggerError
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class DriverRepoImp(supabase: Supabase) : BaseRepoImp(supabase), DriverRepo {

    override suspend fun getDriverOnAuthId(authId: String): Driver? = querySingle(SUPA_DRIVER) {
        Driver::authId eq authId
    }

    override suspend fun getDriverOnEmail(email: String): Driver? = querySingle(SUPA_DRIVER) {
        Driver::email eq email
    }

    override suspend fun getAllDriversOnAuthIds(ids: List<String>): List<Driver> = query(SUPA_DRIVER) {
        Driver::authId isIn ids
    }

    override suspend fun getDriverDetails(id: Long, invoke: suspend (DriverData?) -> Unit) {
        queryWithForeign(SUPA_DRIVER, Columns.raw("*, $SUPA_DRIVER_RATE(*)")) {
            Driver::id eq id
        }?.apply {
            toListOfObject<DriverDetails>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })?.firstOrNull().let { result ->
                toListOfObject<Driver>(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                })?.firstOrNull().let { if (it == Driver()) null else it }?.let { driver ->
                    result?.driverRate?.let { if (it == DriverRate()) null else result.driverRate }?.let { driverRate ->
                        invoke(DriverData(driver, driverRate))
                    } ?: invoke(null)
                }
            }

        } ?: invoke(null)
    }

    override suspend fun getDriverDetailsForAdmin(id: Long, invoke: suspend (DriverAdminData?) -> Unit) {
        queryWithForeign(SUPA_DRIVER, Columns.raw("*, $SUPA_DRIVER_RATE(*), $SUPA_DRIVER_LICENCE(*)")) {
            Driver::id eq id
        }?.apply {
            toListOfObject<DriverAllDetails>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })?.firstOrNull().let { result ->
                toListOfObject<Driver>(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                })?.firstOrNull().let { if (it == Driver()) null else it }?.let { driver ->
                    result?.driverRate?.let { if (it == DriverRate()) null else result.driverRate }?.let { driverRate ->
                        result.driverLicences.let { if (it == DriverLicences()) null else result.driverLicences }?.let { driverLicences ->
                            invoke(DriverAdminData(driver, driverRate, driverLicences))
                        }
                    } ?: invoke(null)
                }
            }

        } ?: invoke(null)
    }

    override suspend fun addNewDriver(item: Driver): Driver? = insert(SUPA_DRIVER, item)

    override suspend fun editDriver(item: Driver): Driver? = edit(SUPA_DRIVER, item.id, item)

    override suspend fun deleteDriver(id: Long): Int = delete(SUPA_DRIVER, id)


    override suspend fun addNewDriverRate(item: DriverRate): DriverRate? = insert(SUPA_DRIVER_RATE, item)

    override suspend fun addEditDriverRate(driverId: Long, rate: Float): Int = try {
        buildJsonObject {
            put("p_driver_id", driverId)
            put("p_rate", rate)
        }.let {
            rpc("update_driver_rate", it)
        }
    } catch (e: Exception) {
        loggerError(error = e)
        -2
    }

    override suspend fun editDriverRate(item: DriverRate): DriverRate? = edit(SUPA_DRIVER_RATE, item.id, item)

    override suspend fun deleteDriverRate(id: Long): Int = delete(SUPA_DRIVER_RATE, id)


    override suspend fun addNewDriverLicences(item: DriverLicences): DriverLicences? = insert(SUPA_DRIVER_LICENCE, item)

    override suspend fun editDriverLicences(item: DriverLicences): DriverLicences? = edit(SUPA_DRIVER_LICENCE, item.id, item)

    override suspend fun deleteDriverLicences(id: Long): Int = delete(SUPA_DRIVER_LICENCE, id)

}