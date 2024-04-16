package com.digitalge.hiddencity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException


class mapa_caminho : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var destinationLat: Double = 0.0
    private var destinationLng: Double = 0.0
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_caminho)

        destinationLat = intent.getDoubleExtra("destination_lat", 0.0)
        destinationLng = intent.getDoubleExtra("destination_lng", 0.0)
        currentLat = intent.getDoubleExtra("current_lat", 0.0)
        currentLng = intent.getDoubleExtra("current_lng", 0.0)

        // Inicializa o Fragmento do Mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val currentLocation = LatLng(currentLat, currentLng)
        val destinationLocation = LatLng(destinationLat, destinationLng)

        // Adiciona marcadores no mapa
        mMap.addMarker(MarkerOptions().position(currentLocation).title("Local Atual"))
        mMap.addMarker(MarkerOptions().position(destinationLocation).title("Destino"))

        // Recupera o nome do local passado pela Intent
        val placeName = intent.getStringExtra("place_name") ?: "Destino"
        mMap.addMarker(MarkerOptions().position(destinationLocation).title(placeName))

        // Move a câmera para o destino
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 15f))

        // Busca e desenha a rota
        drawRoute(currentLocation, destinationLocation)
    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&mode=walking&key=AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk"

        val request = Request.Builder().url(urlDirections).build()
        OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("Map Directions", "Failed to fetch directions", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val jsonData = response.body?.string()
                val jsonObject = JSONObject(jsonData)
                val routes = jsonObject.getJSONArray("routes")

                if (routes.length() > 0) { // Verifica se há rotas disponíveis
                    val overviewPolylines = routes.getJSONObject(0)
                    val encodedString =
                        overviewPolylines.getJSONObject("overview_polyline").getString("points")
                    val list = PolyUtil.decode(encodedString)

                    for (i in 0 until list.size - 1) {
                        val src = list[i]
                        val dest = list[i + 1]
                        path.add(listOf(src, dest))
                    }

                    // Atualiza o mapa na Thread principal
                    runOnUiThread {
                        for (i in path.indices) {
                            mMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.BLUE))
                        }
                    }
                } else {
                    Log.e("Map Directions", "No routes found.")
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "No walking route found",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
    }
}