package com.dev007.livelocationtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.dev007.livelocationtracker.ui.theme.LiveLocationTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )

        setContent {
            LiveLocationTrackerTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🌍 Location Tracker Background Service.!!",
                        fontSize = MaterialTheme.typography.h6.fontSize
                    )
                    Spacer(modifier = Modifier.height(46.dp))

                    Button(onClick = {
                        if (LocationService.isMyServiceRunning) {
                            Toast.makeText(
                                this@MainActivity,
                                "Service Already Running 😪😪...",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_START
                            startService(this)//will send the action and Intent to service
                        }
                    }) {
                        Text(
                            fontSize = MaterialTheme.typography.h6.fontSize,
                            text = if (LocationService.isMyServiceRunning) "Service Started" else "Start 😇"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        if (!LocationService.isMyServiceRunning) {
                            Toast.makeText(
                                this@MainActivity,
                                "Service Not Running...",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_STOP
                            startService(this)//will send the action and Intent to service
                        }
                    }) {
                        Text(fontSize = MaterialTheme.typography.h6.fontSize, text = "Stop 😅")
                    }
                }
            }
        }
    }
}
