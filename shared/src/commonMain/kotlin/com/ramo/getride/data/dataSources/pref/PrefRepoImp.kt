package com.ramo.getride.data.dataSources.pref

import com.ramo.getride.global.base.RealmLocal
import com.ramo.getride.data.model.Preference
import com.ramo.getride.data.util.REALM_FAILED
import com.ramo.getride.data.util.REALM_SUCCESS
import com.ramo.getride.global.util.logger
import io.realm.kotlin.ext.query

class PrefRepoImp(private val realm: RealmLocal) : PrefRepo {

    override suspend fun prefs(invoke: suspend (List<Preference>) -> Unit) {
        return kotlinx.coroutines.coroutineScope {
            kotlin.runCatching {
                realm.query(Preference::class).find()
            }.getOrNull()?.let {
                invoke.invoke(it)
            } ?: invoke.invoke(emptyList())
        }
    }

    override suspend fun insertPref(pref: Preference): Preference? {
        return realm.write {
            try {
                copyToRealm(pref, io.realm.kotlin.UpdatePolicy.ALL)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun insertPref(prefs: List<Preference>, invoke: suspend ((List<Preference>?) -> Unit)) {
        return realm.write {
            try {
                prefs.map { pref ->
                    copyToRealm(pref, io.realm.kotlin.UpdatePolicy.ALL)
                }
            } catch (e: Exception) {
                null
            }
        }.let {
            invoke(it)
        }
    }


    override suspend fun updatePref(pref: Preference, newValue: String): Preference {
        return try {
            realm.write {
                return@write query<Preference>("keyString == $0", pref.keyString).first().find()?.also {
                    it.value = newValue
                }
            } ?: realm.write {
                kotlin.run {
                    copyToRealm(pref, io.realm.kotlin.UpdatePolicy.ALL)
                    pref
                }
            }
        } catch (e: Exception) {
            logger(error = e.stackTraceToString())
            pref
        }
    }

    override suspend fun updatePref(prefs: List<Preference>): List<Preference> {
        return try {
            realm.write {
                return@write prefs.map { pref ->
                    return@map query<Preference>("keyString == $0", pref.keyString).first().find()?.also {
                        it.value = pref.value
                    } ?: kotlin.run {
                        copyToRealm(pref, io.realm.kotlin.UpdatePolicy.ALL)
                        pref
                    }
                }
            }
        } catch (e: Exception) {
            logger(error = e.stackTraceToString())
            prefs
        }
    }

    override suspend fun deletePref(key: String): Int {
        return realm.write {
            try {
                query<Preference>("keyString == $0", key).first().find()?.let {
                    delete(it)
                }
            } catch (e: Exception) {
                return@write null
            }
        }.let {
            return@let if (it == null) REALM_FAILED else REALM_SUCCESS
        }
    }

    override suspend fun deletePrefAll(): Int {
        return realm.write {
            kotlin.runCatching {
                delete(schemaClass = Preference::class)
            }.getOrNull()
        }.let {
            return@let if (it == null) REALM_FAILED else REALM_SUCCESS
        }
    }

}