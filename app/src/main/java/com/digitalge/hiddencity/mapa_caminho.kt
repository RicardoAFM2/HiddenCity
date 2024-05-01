package com.digitalge.hiddencity


import android.os.Bundle
import android.util.Log
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
import okio.IOException
import org.json.JSONObject

class mapa_caminho : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_caminho)

        // Initialize the SupportMapFragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Suponha que você tenha as seguintes coordenadas para origem e destino
        val origin = LatLng(-23.563987, -46.653492)
        val destination = LatLng(-23.550520, -46.633308)

        // Adicione marcadores para origem e destino
        map.addMarker(MarkerOptions().position(origin).title("Origem"))
        map.addMarker(MarkerOptions().position(destination).title("Destino"))

        // Move a câmera para a origem
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15f))

        // Chame a função para desenhar o caminho mais rápido
        drawRoute(origin, destination)
    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {
        val apiKey = "AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk" // Substitua pela sua chave da API
        val directionsUrl = getDirectionsUrl(origin, destination, apiKey)

        // Faz a chamada HTTP para a Directions API
        val client = OkHttpClient()
        val request = Request.Builder().url(directionsUrl).build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("DirectionsAPI", "Falha na chamada da API de direções", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseString = response.body?.string() ?: return
                val jsonResponse = JSONObject(responseString)
                Log.d("DirectionsAPI", "Resposta da API de direções: $responseString")

                // Obtém o polyline da rota
                val routes = jsonResponse.getJSONArray("routes")
                if (routes.length() > 0) {
                    val route = routes.getJSONObject(0)
                    val polyline = route.getJSONObject("overview_polyline").getString("points")

                    // Desenha o polyline no mapa
                    val decodedPath = PolyUtil.decode(polyline)
                    runOnUiThread {
                        map.addPolyline(PolylineOptions().addAll(decodedPath))
                    }
                }
            }
        })
    }

    private fun getDirectionsUrl(origin: LatLng, destination: LatLng, apiKey: String): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${destination.latitude},${destination.longitude}"
        val sensor = "sensor=false"
        val mode = "mode=walking" // Modo de caminhada
        val parameters = "$strOrigin&$strDest&$sensor&$mode"
        val output = "json"

        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=$apiKey"
    }
}


