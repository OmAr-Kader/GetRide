package com.ramo.getride.android.global.util

inline fun android.content.Context.checkLocationPermission(invoke: () -> Unit, failed: (Array<String>) -> Unit) {
    if (androidx.core.app.ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED || androidx.core.app.ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    ) {
        invoke()
    } else {
        failed(
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }
}

fun android.content.Context.checkLocationStates(invoke: () -> Unit, failed: (androidx.activity.result.IntentSenderRequest) -> Unit) {
    com.google.android.gms.location.LocationSettingsRequest.Builder()
        .addLocationRequest(
            com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                5000L // Update interval in milliseconds
            ).build())
        .setAlwaysShow(true) // Shows a dialog to the user to turn on location services
        .build().also { locationSettingsRequest ->
            com.google.android.gms.location.LocationServices.getSettingsClient(this).checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    invoke()
                }
                .addOnFailureListener { exception ->
                    if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                        try {
                            androidx.activity.result.IntentSenderRequest.Builder(exception.resolution).build().also(failed)
                        } catch (sendEx: android.content.IntentSender.SendIntentException) {
                            // Handle the error
                        }
                    } else {
                        invoke()
                    }
                }
        }
}

@android.annotation.SuppressLint("MissingPermission")
fun com.google.android.gms.location.FusedLocationProviderClient.fetchLastKnownLocation(invoke: (com.google.android.gms.maps.model.LatLng) -> Unit) {
    lastLocation.addOnSuccessListener {
        it?.also { location ->
            com.google.android.gms.maps.model.LatLng(location.latitude, location.longitude).also(invoke)
        }
    }
}