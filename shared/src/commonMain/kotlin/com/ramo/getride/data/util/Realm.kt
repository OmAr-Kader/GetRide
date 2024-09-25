package com.ramo.getride.data.util

import com.ramo.getride.data.model.Preference

const val REALM_SUCCESS: Int = 1
const val REALM_FAILED: Int = -1

inline val listOfOnlyLocalSchemaRealmClass: Set<kotlin.reflect.KClass<out io.realm.kotlin.types.TypedRealmObject>>
    get() = setOf(Preference::class)