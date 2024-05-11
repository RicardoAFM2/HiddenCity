package com.digitalge.hiddencity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject

class mapa_caminho : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var origin: LatLng
    private lateinit var destination: LatLng
    private var locationMarker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_caminho)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val placeName = intent.getStringExtra("place_name")
        val tvPlaceName = findViewById<TextView>(R.id.tvPlaceName)
        tvPlaceName.text = placeName


        // Initialize the SupportMapFragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            onMapReady(googleMap)
        }

        setupModeButtons()
    }

    private fun setupModeButtons() {
        findViewById<Button>(R.id.btnDrive).setOnClickListener {
            updateRoute("driving")
        }
        findViewById<Button>(R.id.btnWalk).setOnClickListener {
            updateRoute("walking")
        }
        findViewById<Button>(R.id.btnBike).setOnClickListener {
            updateRoute("bicycling")
        }
        findViewById<Button>(R.id.btnTransit).setOnClickListener {
            updateRoute("transit")
        }
        findViewById<ImageView>(R.id.btnClose).setOnClickListener { onBackPressed() }
    }

    private fun updateRoute(mode: String) {
        if (::origin.isInitialized && ::destination.isInitialized) {
            // Limpa o mapa antes de desenhar a nova rota para o modo escolhido
            map.clear()
            // Adiciona marcadores novamente após limpar o mapa
            map.addMarker(MarkerOptions().position(origin).title("Origem"))
            map.addMarker(MarkerOptions().position(destination).title("Destino"))
            // Chama a função para desenhar a rota com o novo modo
            drawRoute(origin, destination, mode)
        } else {
            Toast.makeText(this, "Localização ainda não definida.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return  // Sair do método se as permissões não forem concedidas
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                updateLocation(LatLng(it.latitude, it.longitude))
                origin = LatLng(it.latitude, it.longitude)
                destination = LatLng(intent.getDoubleExtra("local_lat", 0.0), intent.getDoubleExtra("local_lng", 0.0))  // Definindo a variável de classe

                // Adicione marcadores para origem e destino
                map.addMarker(MarkerOptions().position(origin).title("Origem"))
                map.addMarker(MarkerOptions().position(destination).title("Destino"))

                // Move a câmera para incluir ambos os pontos no centro do mapa
                val bounds = LatLngBounds.Builder()
                    .include(origin)
                    .include(destination)
                    .build()
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

                // Inicializa a rota inicial
                drawRoute(origin, destination, "driving")
            } ?: Toast.makeText(this, "Não foi possível obter a localização atual.", Toast.LENGTH_LONG).show()
        }
        startLocationUpdates()
    }

    private fun updateLocation(location: LatLng) {
        if (locationMarker == null) {
            locationMarker = map.addMarker(MarkerOptions().position(location).title("A minha Localização"))
        } else {
            locationMarker?.position = location
        }
    }
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000  // 10 segundos
            fastestInterval = 5000  // 5 segundos
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    val newLoc = LatLng(it.latitude, it.longitude)
                    updateLocation(newLoc)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permissões
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }


    private fun drawRoute(origin: LatLng, destination: LatLng, mode: String) {
        val apiKey = "AIzaSyAY0UR5MevkM3u9EvJ-cgVqXjBH66uF5y0" // Substitua pela sua chave da API
        val directionsUrl = getDirectionsUrl(origin, destination, mode, apiKey)

        // Faz a chamada HTTP para a Directions API
        val client = OkHttpClient()
        val request = Request.Builder().url(directionsUrl).build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("DirectionsAPI", "Falha na chamada da API de direções", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseString = response.body?.string() ?: return
                Log.d("API Response", responseString)  // Log da resposta completa
                val jsonResponse = JSONObject(responseString)

                if (jsonResponse.getString("status") == "OK") {
                    val routes = jsonResponse.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val route = routes.getJSONObject(0)
                        val polyline = route.getJSONObject("overview_polyline").getString("points")
                        val decodedPath = PolyUtil.decode(polyline)
                        val duration = route.getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text")

                        runOnUiThread {
                            map.clear()  // Limpa rotas antigas e marcadores

                            // Adiciona marcadores para origem e destino
                            map.addMarker(MarkerOptions().position(origin).title("Origem"))
                            map.addMarker(MarkerOptions().position(destination).title("Destino"))

                            // Adiciona a rota decodificada ao mapa
                            map.addPolyline(PolylineOptions().addAll(decodedPath).color(Color.BLUE)) // Ajuste a cor conforme necessário

                            // Atualiza o tempo estimado de viagem
                            val tvEstimatedTime = findViewById<TextView>(R.id.tvEstimatedTime)
                            tvEstimatedTime.text = "Tempo estimado: $duration, Modo: ${mode.capitalize()}"
                        }
                    }
                } else {
                    Log.e("DirectionsAPI", "Erro na resposta da API: ${jsonResponse.getString("status")}")
                }
            }
        })
    }

    private fun getDirectionsUrl(origin: LatLng, destination: LatLng, mode: String, apiKey: String): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${destination.latitude},${destination.longitude}"
        val parameters = "$strOrigin&$strDest&sensor=false&mode=$mode"
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=$apiKey"
    }
}
