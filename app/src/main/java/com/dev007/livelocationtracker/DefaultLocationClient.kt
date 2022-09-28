package com.dev007.livelocationtracker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private var context: Context,
    private var client: FusedLocationProviderClient
) : LocationClient {


    @SuppressLint("MissingPermission") //already added permission via extension function
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow { //we can use this, when we need a callback function or response coming frequently.
            //here in this case our Client FusedLocationProviderClient will give callback response frequently whenever something triggered.
            if (!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Missing Location Permission.")
            }

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled || !isNetworkEnabled) {
                throw LocationClient.LocationException("Gps is Disabled.")
            }

            val request = com.google.android.gms.location.LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(interval)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(res: LocationResult) {
                    super.onLocationResult(res)
                    res.locations.lastOrNull()?.let { location ->
                        launch { send(location) } //it will send to kotlin flow
                    }
                }
            }


            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose { //this will block the flow until it's scope closed.
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}