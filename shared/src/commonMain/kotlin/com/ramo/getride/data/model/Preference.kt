package com.ramo.getride.data.model

open class Preference(
    @io.realm.kotlin.types.annotations.PrimaryKey
    var _id: org.mongodb.kbson.ObjectId = org.mongodb.kbson.ObjectId.invoke(),
    @io.realm.kotlin.types.annotations.Index
    var keyString: String,
    var value: String,
) : io.realm.kotlin.types.RealmObject {
    constructor() : this(org.mongodb.kbson.ObjectId.invoke(), "", "")
    constructor(keyString: String, value: String) : this(org.mongodb.kbson.ObjectId.invoke(), keyString, value)
    constructor(pref: PreferenceData) : this(if (pref.id.isEmpty()) org.mongodb.kbson.ObjectId.invoke() else org.mongodb.kbson.ObjectId.invoke(pref.id), pref.keyString, pref.value)
}

data class PreferenceData(
    val id: String,
    val keyString: String,
    val value: String,
) {
    constructor(keyString: String, value: String) : this("", keyString, value)
    constructor(pref: Preference) : this(pref._id.toHexString(), pref.keyString, pref.value)
}