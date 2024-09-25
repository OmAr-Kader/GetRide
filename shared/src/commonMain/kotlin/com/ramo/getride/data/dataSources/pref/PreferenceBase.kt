package com.ramo.getride.data.dataSources.pref

import com.ramo.getride.data.model.Preference
import com.ramo.getride.data.model.PreferenceData
import com.ramo.getride.data.util.toPreference
import com.ramo.getride.data.util.toPreferenceDate

class PreferenceBase(
    private val repository: PrefRepo
) {

    suspend fun prefs(invoke: (List<PreferenceData>) -> Unit): Unit = repository.prefs {
        invoke(it.toPreferenceDate())
    }

    /*suspend fun insertPref(pref: PreferenceData): PreferenceData? = repository.insertPref(Preference(pref)).let { it?.let { it1 -> PreferenceData(it1) } }

    suspend fun insertPref(prefs: List<PreferenceData>, invoke: ((List<PreferenceData>?) -> Unit)) {
        repository.insertPref(prefs.toPreference()) {
            invoke(it?.toPreferenceDate())
        }
    }*/

    suspend fun updatePref(pref: PreferenceData, newValue: String): PreferenceData = PreferenceData(repository.updatePref(Preference(pref), newValue))

    suspend fun updatePref(
        pref: List<PreferenceData>
    ): List<PreferenceData> = repository.updatePref(pref.toPreference()).toPreferenceDate()

    suspend fun deletePref(key: String): Int = repository.deletePref(key)

    suspend fun deletePrefAll(): Int= repository.deletePrefAll()

}
