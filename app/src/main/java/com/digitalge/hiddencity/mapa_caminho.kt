package com.digitalge.hiddencity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject
import okhttp3.*
import org.json.JSONArray


class mapa_caminho : AppCompatActivity(), OnMapReadyCallback {

    private var lastKnownLocation: LatLng? = null
    private var polyline: Polyline? = null


    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private var stepsList: List<Step> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_caminho)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val placeName = intent.getStringExtra("place_name")
        val tvPlaceName = findViewById<TextView>(R.id.tvPlaceName)
        tvPlaceName.text = placeName


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnDrive).setOnClickListener { updateRoute("driving") }
        findViewById<Button>(R.id.btnWalk).setOnClickListener { updateRoute("walking") }
        findViewById<Button>(R.id.btnBike).setOnClickListener { updateRoute("bicycling") }
        findViewById<Button>(R.id.btnTransit).setOnClickListener { updateRoute("transit") }

        val btnClose = findViewById<ImageView>(R.id.btnClose)

        // Defina um OnClickListener para o botão
        btnClose.setOnClickListener {
            // Finaliza a atividade atual e retorna para a anterior na pilha
            onBackPressed()
        }
    }

    private fun updateRoute(mode: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val origin = LatLng(it.latitude, it.longitude)
                val destinationLat = intent.getDoubleExtra("local_lat", 0.0)
                val destinationLng = intent.getDoubleExtra("local_lng", 0.0)
                val destination = LatLng(destinationLat, destinationLng)

                drawRoute(origin, destination, mode)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        updateLocationUI()

        val destinationLat =
            intent.getDoubleExtra("local_lat", 0.0) // Valor padrão 0.0 se não for encontrado
        val destinationLng =
            intent.getDoubleExtra("local_lng", 0.0) // Valor padrão 0.0 se não for encontrado
        val destination = LatLng(destinationLat, destinationLng)

        map.addMarker(MarkerOptions().position(destination).title("Destino"))
        map.uiSettings.isZoomControlsEnabled = false
        map.uiSettings.isZoomGesturesEnabled = true

        // Checar permissões de localização
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            return
        }
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLocation = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20f))
            }
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLocation = LatLng(it.latitude, it.longitude)
                drawRoute(currentLocation, destination, "walking")
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20f))
            }
        }

        startLocationUpdates()

    }


    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Intervalo de 5 segundos para atualizações
            fastestInterval = 2000 // O intervalo mais rápido de 2 segundos
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Alta precisão
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val newLoc = LatLng(location.latitude, location.longitude)
                    if (lastKnownLocation == null || isSignificantlyFar(newLoc, lastKnownLocation!!)) {
                        lastKnownLocation = newLoc
                        updateRouteToDestination(newLoc) // Atualiza a rota a partir da nova localização
                    }
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun isSignificantlyFar(currentLoc: LatLng, lastLoc: LatLng): Boolean {
        val distance = FloatArray(1)
        Location.distanceBetween(
            currentLoc.latitude, currentLoc.longitude,
            lastLoc.latitude, lastLoc.longitude,
            distance
        )
        return distance[0] > 50 // Distância significativa para recalcular a rota
    }

    private fun updateRouteToDestination(currentLocation: LatLng) {
        val destinationLat = intent.getDoubleExtra("local_lat", 0.0)
        val destinationLng = intent.getDoubleExtra("local_lng", 0.0)
        val destination = LatLng(destinationLat, destinationLng)
        drawRoute(currentLocation, destination, "walking") // Assumindo modo a pé como exemplo
    }

    private fun showArrivalPopup() {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("Chegada ao Destino")
                .setMessage("Você chegou ao seu destino!")
                .setPositiveButton("OK") { dialog, which ->
                    finish() // Fecha a atividade e retorna para a atividade anterior
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun updateLocationUI() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permissões concedidas, reiniciar funcionalidades que precisam de localização
            mapFragment.getMapAsync(this)
        }
    }


    private fun drawRoute(origin: LatLng, destination: LatLng, mode: String) {
        val apiKey = "AIzaSyAY0UR5MevkM3u9EvJ-cgVqXjBH66uF5y0" // Substitua pela sua chave da API
        val modeDisplayName = getModeDisplayName(mode)
        // Incluindo o parâmetro de idioma na URL
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&mode=$mode&language=pt-BR&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Tratar falhas na chamada
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()
                if (response.isSuccessful && jsonData != null) {
                    val duration = parseDuration(jsonData)
                    runOnUiThread {
                        val firstStep = stepsList.firstOrNull()
                        findViewById<TextView>(R.id.tvNavigationInstruction).text = firstStep?.instruction ?: "Inicie sua rota"
                        val tvEstimatedTime = findViewById<TextView>(R.id.tvEstimatedTime)
                        tvEstimatedTime.text = "Tempo estimado: $duration, Modo: $modeDisplayName"
                        drawPath(jsonData)
                    }
                }
            }
        })
    }

    private fun drawPath(jsonData: String) {
        val jsonObject = JSONObject(jsonData)
        val routes = jsonObject.getJSONArray("routes")
        if (routes.length() > 0) {
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            if (legs.length() > 0) {
                val route = routes.getJSONObject(0)
                val overviewPolyline = route.getJSONObject("overview_polyline")
                val points = overviewPolyline.getString("points")
                val decodedPath = PolyUtil.decode(points)
                runOnUiThread {
                    map.clear() // Limpa o mapa antes de desenhar a nova rota
                    map.addPolyline(PolylineOptions().addAll(decodedPath).width(12f).color(Color.RED).geodesic(true))
//                    polyline?.remove()  // Remove the old polyline
                    polyline = map.addPolyline(
                        PolylineOptions()
                            .addAll(decodedPath)
                            .width(12f)
                            .color(Color.RED)
                            .geodesic(true)
                    )
                }
                updateStepsAndInstructions(legs)

                // Aqui atualizamos a lista de passos e a interface do usuário
                stepsList = mutableListOf<Step>().apply {
                    val stepsJson = legs.getJSONObject(0).getJSONArray("steps")
                    for (i in 0 until stepsJson.length()) {
                        val step = stepsJson.getJSONObject(i)
                        val instruction = Html.fromHtml(step.getString("html_instructions")).toString()  // Convert HTML to plain text
                        val distance = step.getJSONObject("distance").getInt("value")
                        add(Step(instruction, distance))
                    }
                }

                // Atualizar a UI com a primeira etapa
                if (stepsList.isNotEmpty()) {
                    runOnUiThread {
                        findViewById<TextView>(R.id.tvNavigationInstruction).text = stepsList.first().instruction
                    }
                }
            }
        }
    }

    private fun updateStepsAndInstructions(legs: JSONArray) {
        // Here we parse steps and update UI as before
        stepsList = mutableListOf<Step>().apply {
            val stepsJson = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until stepsJson.length()) {
                val step = stepsJson.getJSONObject(i)
                val instruction = Html.fromHtml(step.getString("html_instructions")).toString()
                val distance = step.getJSONObject("distance").getInt("value")
                add(Step(instruction, distance))
            }
        }

        // Update the UI with the first step
        if (stepsList.isNotEmpty()) {
            runOnUiThread {
                findViewById<TextView>(R.id.tvNavigationInstruction).text = stepsList.first().instruction
            }
        }
    }

    data class Step(val instruction: String, val distance: Int)

    private fun parseDuration(jsonData: String): String {
        val jsonObject = JSONObject(jsonData)
        val routes = jsonObject.getJSONArray("routes")
        if (routes.length() > 0) {
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            if (legs.length() > 0) {
                return legs.getJSONObject(0).getJSONObject("duration").getString("text")
            }
        }
        return "indisponível"
    }

    private fun getModeDisplayName(mode: String): String {
        return when (mode) {
            "driving" -> "Carro"
            "walking" -> "A pé"
            "bicycling" -> "Bicicleta"
            "transit" -> "Transporte Público"
            else -> mode
        }
    }
}