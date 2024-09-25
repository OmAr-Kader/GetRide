package com.ramo.getride.data.dataSources.pref

import com.ramo.getride.data.model.Preference

interface PrefRepo {

    suspend fun prefs(invoke: suspend (List<Preference>) -> Unit)

    suspend fun insertPref(pref: Preference): Preference?

    suspend fun insertPref(prefs: List<Preference>, invoke: suspend ((List<Preference>?) -> Unit))

    suspend fun updatePref(pref: Preference, newValue: String): Preference

    suspend fun updatePref(prefs: List<Preference>): List<Preference>

    suspend fun deletePref(key: String): Int

    suspend fun deletePrefAll(): Int

}