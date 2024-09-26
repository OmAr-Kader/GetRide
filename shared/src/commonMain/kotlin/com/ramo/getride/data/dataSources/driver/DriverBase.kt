package com.ramo.getride.data.dataSources.driver

import com.ramo.getride.data.model.Driver
import com.ramo.getride.data.model.DriverAdminData
import com.ramo.getride.data.model.DriverData
import com.ramo.getride.data.model.DriverLicences
import com.ramo.getride.data.model.DriverRate

class DriverBase(
    private val repo: DriverRepo
) {

    suspend fun getDriverOnAuthId(authId: String): Driver? = repo.getDriverOnAuthId(authId)
    suspend fun getDriverOnEmail(email: String): Driver? = repo.getDriverOnEmail(email)
    suspend fun getAllDriversOnAuthIds(ids: List<String>): List<Driver> = repo.getAllDriversOnAuthIds(ids)
    suspend fun getDriverDetails(id: Long, invoke: suspend (DriverData?) -> Unit) = repo.getDriverDetails(id, invoke)
    suspend fun getDriverDetailsForAdmin(id: Long, invoke: suspend (DriverAdminData?) -> Unit) = repo.getDriverDetailsForAdmin(id, invoke)
    suspend fun addNewDriver(item: Driver): Driver? = repo.addNewDriver(item)
    suspend fun editDriver(item: Driver): Driver? = repo.editDriver(item)
    suspend fun deleteDriver(id: Long): Int = repo.deleteDriver(id)

    suspend fun addNewDriverRate(item: DriverRate): DriverRate? = repo.addNewDriverRate(item)
    suspend fun editDriverRate(item: DriverRate): DriverRate? = repo.editDriverRate(item)
    suspend fun deleteDriverRate(id: Long): Int = repo.deleteDriverRate(id)

    suspend fun addNewDriverLicences(item: DriverLicences): DriverLicences? = repo.addNewDriverLicences(item)
    suspend fun editDriverLicences(item: DriverLicences): DriverLicences? = repo.editDriverLicences(item)
    suspend fun deleteDriverLicences(id: Long): Int = repo.deleteDriverLicences(id)

}