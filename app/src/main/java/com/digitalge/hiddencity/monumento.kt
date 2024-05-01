package com.digitalge.hiddencity


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Adapter.MonumentAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient


class monumento : AppCompatActivity() {


    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val radiusInMeters = 5000.0

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monumento)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk")
        }
        placesClient = Places.createClient(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val recyclerView: RecyclerView = findViewById(R.id.results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MonumentAdapter(emptyList(), placesClient)

        // Solicitar permissão de localização
        checkLocationPermission()
    }




    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            getCurrentLocation()
        }
    }

    private fun fetchNearbyMonuments(userLocation: Location) {
        // Defina os campos que você deseja receber para cada lugar encontrado
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.PHOTO_METADATAS,
            Place.Field.LAT_LNG,
            Place.Field.TYPES
        )

        // Crie a requisição com os campos definidos
        val request = FindCurrentPlaceRequest.newInstance(placeFields)

        // Cheque se você tem a permissão ACCESS_FINE_LOCATION, que é necessária para buscar o lugar atual
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Caso não tenha a permissão, solicite-a ao usuário
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            return
        }

        // Execute a requisição para encontrar o lugar atual
        placesClient.findCurrentPlace(request).addOnSuccessListener { response ->
            // Filtre os resultados baseado em se eles são monumentos e se estão dentro do raio especificado
            Log.d("MonumentActivity", "Locais retornados pela API: ${response.placeLikelihoods.size}")
            val nearbyMonuments = response.placeLikelihoods.filter {
                Log.d("MonumentActivity", "Local: ${it.place.name}, Tipos: ${it.place.types}")

                it.place.types?.any { type -> isMonument(type) } == true &&
                        isWithinRadius(userLocation, it.place.latLng, radiusInMeters)
            }.map { it.place }

            Log.d("MonumentActivity", "Encontrados ${nearbyMonuments.size} monumentos dentro de 15 km.")
            nearbyMonuments.forEach { place ->
                Log.d("MonumentActivity", "Monumento: ${place.name}, Localização: ${place.latLng}")
            }

            // Atualize a RecyclerView com os lugares encontrados
            updateRecyclerView(nearbyMonuments)
        }.addOnFailureListener { exception ->
            // Trate o erro ocorrido ao buscar os lugares
            Toast.makeText(this, "Erro ao buscar monumentos: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
            Log.e("MonumentActivity", "Erro ao buscar monumentos: ${exception.localizedMessage}")
        }
    }


    private fun isMonument(type: Place.Type): Boolean {
        val monumentTypes = setOf(
            Place.Type.MUSEUM,
            Place.Type.ART_GALLERY,
            Place.Type.CEMETERY,
            Place.Type.CHURCH,
            Place.Type.HINDU_TEMPLE,
            Place.Type.MOSQUE,
            Place.Type.SYNAGOGUE,
            Place.Type.TOURIST_ATTRACTION,
            Place.Type.UNIVERSITY,
            Place.Type.STADIUM,
            Place.Type.SUBWAY_STATION
        )

        // Verifica se a lista de tipos do lugar inclui algum dos tipos considerados monumentos.
        return type in monumentTypes
    }


    private fun isWithinRadius(
        userLocation: Location,
        placeLocation: LatLng?,
        radiusInMeters: Double
    ): Boolean {
        placeLocation ?: return false
        val result = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            placeLocation.latitude,
            placeLocation.longitude,
            result
        )
        return result[0] <= radiusInMeters
    }

    private fun updateRecyclerView(places: List<Place>) {
        val recyclerView: RecyclerView = findViewById(R.id.results_recycler_view)
        if (places.isNotEmpty()) {
            // Se houver lugares encontrados, esconda a mensagem de "nenhum resultado"
            findViewById<TextView>(R.id.text_view_no_results).visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = MonumentAdapter(places, placesClient) // Não esqueça de passar o placesClient para o adapter
        } else {
            // Se não houver lugares, mostre a mensagem de "nenhum resultado"
            recyclerView.visibility = View.GONE
            findViewById<TextView>(R.id.text_view_no_results).visibility = View.VISIBLE
        }
    }



    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    Log.d("MonumentActivity", "Localização atual: ${it.latitude}, ${it.longitude}")
                    fetchNearbyMonuments(it)
                } ?: run {
                    Toast.makeText(this, "Não foi possível obter a localização.", Toast.LENGTH_LONG).show()
                    Log.e("MonumentActivity", "Não foi possível obter a localização.")
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_LONG).show()
            }
        }
    }


}

