package com.ramo.getride.data.dataSources.driver

import com.ramo.getride.data.model.Driver
import com.ramo.getride.data.model.DriverAdminData
import com.ramo.getride.data.model.DriverData
import com.ramo.getride.data.model.DriverLicences
import com.ramo.getride.data.model.DriverRate

interface DriverRepo {

    suspend fun getDriverOnAuthId(authId: String): Driver?
    suspend fun getDriverOnEmail(email: String): Driver?
    suspend fun getAllDriversOnAuthIds(ids: List<String>): List<Driver>
    suspend fun getDriverDetails(id: Long, invoke: suspend (DriverData?) -> Unit)
    suspend fun getDriverDetailsForAdmin(id: Long, invoke: suspend (DriverAdminData?) -> Unit)
    suspend fun addNewDriver(item: Driver): Driver?
    suspend fun editDriver(item: Driver): Driver?
    suspend fun deleteDriver(id: Long): Int

    suspend fun addNewDriverRate(item: DriverRate): DriverRate?
    suspend fun editDriverRate(item: DriverRate): DriverRate?
    suspend fun deleteDriverRate(id: Long): Int

    suspend fun addNewDriverLicences(item: DriverLicences): DriverLicences?
    suspend fun editDriverLicences(item: DriverLicences): DriverLicences?
    suspend fun deleteDriverLicences(id: Long): Int
}