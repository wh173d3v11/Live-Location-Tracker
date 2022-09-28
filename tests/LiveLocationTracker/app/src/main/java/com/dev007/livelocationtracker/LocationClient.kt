package com.dev007.livelocationtracker

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient { //abstraction.


    fun getLocationUpdates(interval:Long):Flow<Location>

    //in case of error,(If GPS is Off,Disabled)
    class LocationException(message:String):Exception()
}