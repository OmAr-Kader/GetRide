package com.ramo.getride.global.base

const val SCHEMA_LOCAL_VERSION: Long = 1L

const val SPLASH_SCREEN_ROUTE: String = "splash_screen_route"

const val AUTH_SCREEN_ROUTE: String = "auth_screen_route"
const val AUTH_SCREEN_DRIVER_ROUTE: String = "auth_driver_screen_route"
const val HOME_SCREEN_ROUTE: String = "home_screen_route"
const val HOME_SCREEN_DRIVER_ROUTE: String = "home_driver_screen_route"

const val PREF_NAME: String = "name"
const val PREF_PROFILE_IMAGE: String = "profile_image"
const val PREF_LAST_LATITUDE: String = "last_latitude"
const val PREF_LAST_LONGITUDE: String = "last_longitude"

const val TEMP_IS_DRIVER: Boolean = false // @OmAr-Kader Temp

const val CONST_MAP_DARK_MODE: String = "[{\"elementType\":\"geometry\",\"stylers\":[{\"color\":\"#242f3e\"}]},{\"elementType\":\"labels.text.fill\",\"stylers\":[{\"color\":\"#746855\"}]},{\"elementType\":\"labels.text.stroke\",\"stylers\":[{\"color\":\"#242f3e\"}]},{\"featureType\":\"administrative.locality\",\"elementType\":\"labels.text.fill\",\"stylers\":[{\"color\":\"#d59563\"}]},{\"featureType\":\"poi\",\"elementType\":\"labels.text.fill\",\"stylers\":[{\"color\":\"#d59563\"}]},{\"featureType\":\"poi.park\",\"elementType\":\"geometry\",\"stylers\":[{\"color\":\"#263c3f\"}]},{\"featureType\":\"poi.park\",\"elementType\":\"labels.text.fill\",\"stylers\":[{\"color\":\"#6b9a76\"}]},{\"featureType\":\"road\",\"elementType\":\"geometry\",\"stylers\":[{\"color\":\"#38414e\"}]},{\"featureType\":\"road\",\"elementType\":\"geometry.stroke\",\"stylers\":[{\"color\":\"#212a37\"}]},{\"featureType\":\"road\",\"elementType\":\"labels.text.fill\",\"stylers\":[{\"color\":\"#9ca5b3\"}]},{\"featureType\":\"road.highway\",\"elementType\":\"geometry\",\"stylers\":[{\"color\":\"#746855\"}]},{\"featureType\":\"road.highway\",\"elementType\":\"geometry.stroke\",\"stylers\":[{\"color\":\"#1f2835\"}]},{\"featureType\":\"road.highway\",\"elementType\":\"labels.text.fill\",\"stylers\":[{\"color\":\"#f3d19c\"}]},{\"featureType\":\"transit\",\"elementType\":\"geometry\",\"stylers\":[{\"color\":\"#2f3948\"}]},{\"featureType\":\"transit.station\",\"elementType\":\"labels.text.fill\",\"stylers\":[{\"color\":\"#d59563\"}]},{\"featureType\":\"water\",\"elementType\":\"geometry\",\"stylers\":[{\"color\":\"#17263c\"}]},{\"featureType\":\"water\",\"elementType\":\"labels.text.fill\",\"stylers\":[{\"color\":\"#515c6d\"}]},{\"featureType\":\"water\",\"elementType\":\"labels.text.stroke\",\"stylers\":[{\"color\":\"#17263c\"}]}]"

const val BASE_FARE: Double = 2.00
const val COST_PER_MINUTE: Double = 0.05
const val COST_PER_KILOMETER: Double = 0.10