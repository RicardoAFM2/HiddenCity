package com.digitalge.hiddencity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.StreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng

class local_3d : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local3d)

        val streetViewPanoramaFragment = fragmentManager
            .findFragmentById(R.id.streetviewpanorama) as StreetViewPanoramaFragment

        streetViewPanoramaFragment.getStreetViewPanoramaAsync { panorama ->
            // Check if intent has latitude and longitude
            intent?.let {
                val lat = it.getDoubleExtra("local_lat", 0.0)
                val lng = it.getDoubleExtra("local_lng", 0.0)
                panorama.setPosition(LatLng(lat, lng))
            }
        }
    }
}