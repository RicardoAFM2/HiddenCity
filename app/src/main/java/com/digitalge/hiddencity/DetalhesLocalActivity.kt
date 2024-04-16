package com.digitalge.hiddencity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.SphericalUtil
import java.util.Locale
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException


class DetalhesLocalActivity : AppCompatActivity() {
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private  var placeID: String? = null
    private val REQUEST_LOCATION_PERMISSION = 1
    private var photoMetadataList: List<PhotoMetadata> = emptyList()
    private var currentPhotoIndex = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_local)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk")
        }
        placesClient = Places.createClient(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        placeID = intent.getStringExtra("place_id")
        if (placeID == null) {
            Log.e("DetalhesLocalActivity", "No place ID found in intent extras")
            finish()
            return
        }

        setupNavigationButtons()
        checkLocationPermissionAndFetch()
    }

    private fun setupNavigationButtons() {
        findViewById<Button>(R.id.image_next_button).setOnClickListener {
            showNextPhoto()
        }
        findViewById<Button>(R.id.image_prev_button).setOnClickListener {
            showPreviousPhoto()
        }
    }

    private fun showNextPhoto() {
        if (photoMetadataList.isNotEmpty()) {
            currentPhotoIndex = (currentPhotoIndex + 1) % photoMetadataList.size
            loadPhoto(photoMetadataList[currentPhotoIndex])
        }
    }

    private fun showPreviousPhoto() {
        if (photoMetadataList.isNotEmpty()) {
            currentPhotoIndex = if (currentPhotoIndex > 0) currentPhotoIndex - 1 else photoMetadataList.size - 1
            loadPhoto(photoMetadataList[currentPhotoIndex])
        }
    }

    private fun loadPhoto(photoMetadata: PhotoMetadata) {
        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
            .setMaxWidth(resources.getDimensionPixelSize(R.dimen.default_image_width))
            .setMaxHeight(resources.getDimensionPixelSize(R.dimen.default_image_height))
            .build()
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
            findViewById<ImageView>(R.id.local_image_view).setImageBitmap(fetchPhotoResponse.bitmap)
        }.addOnFailureListener { e ->
            Log.e("API Error", "Error fetching photo: ${e.message}")
        }
    }

    private fun checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }
    private fun fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { updateDistanceAndDescription(it) }
            }.addOnFailureListener { e ->
                Log.e("Location Error", "Error getting location: ${e.message}")
            }
        }
    }

    private fun updateUIWithPlaceDetails(place: Place, currentLocation: Location) {
        findViewById<TextView>(R.id.local_name_text_view).text = place.name
        place.latLng?.let { latLng ->
            val distance = SphericalUtil.computeDistanceBetween(
                LatLng(currentLocation.latitude, currentLocation.longitude), latLng)
            findViewById<TextView>(R.id.local_distance_text_view).apply {
                text = "${(distance / 1000).toInt()} Km da sua localização"
                setOnClickListener {
                    val intent = Intent(this@DetalhesLocalActivity, mapa_caminho::class.java).apply {
                        putExtra("destination_lat", latLng.latitude)
                        putExtra("destination_lng", latLng.longitude)
                        putExtra("current_lat", currentLocation.latitude)
                        putExtra("current_lng", currentLocation.longitude)
                        putExtra("place_name", place.name)
                    }
                    startActivity(intent)
                }
            }
        }

        // Exibir a classificação numérica
        place.rating?.let {
            findViewById<TextView>(R.id.local_rating_text_view).text = "${it}"
        }

        // Exibir os tipos de lugar
        place.types?.let { types ->
            findViewById<TextView>(R.id.local_description_text_view).text = types.joinToString(separator = ", ") {
                it.name.replace("_", " ").toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
            }
        }

        // Carregar e exibir fotos
        photoMetadataList = place.photoMetadatas ?: emptyList()
        if (photoMetadataList.isNotEmpty()) loadPhoto(photoMetadataList.first())

        // Mostrar a historia de uma local
        place.name?.let { name ->
            fetchSummaryFromWikipedia(name) { summary ->
                runOnUiThread {
                    findViewById<TextView>(R.id.local_description_text_view).text = summary
                }
            }
        }
    }

    private fun updateDistanceAndDescription(currentLocation: Location) {
        placeID?.let { pid ->
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.TYPES,
                Place.Field.RATING
            )
            val request = FetchPlaceRequest.newInstance(pid, placeFields)
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                runOnUiThread {
                    updateUIWithPlaceDetails(place, currentLocation)
                }
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    Log.e("API Error", "Error fetching place details: ${exception.statusCode}")
                } else {
                    Log.e("API Error", "An unexpected error occurred: ${exception.message}")
                }
            }
        }
    }


    fun fetchSummaryFromWikipedia(placeName: String, callback: (String) -> Unit) {
        val formattedName = placeName.replace(" ", "_") // Formatar o nome para URL
        val url = "https://pt.wikipedia.org/api/rest_v1/page/summary/$formattedName"

        val request = Request.Builder().url(url).build()

        OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback("Falha ao buscar dados: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let {
                    val jsonObject = JSONObject(it)
                    val extract = jsonObject.optString("extract", "Resumo não disponível.")
                    callback(extract)
                } ?: callback("Resumo não disponível.")
            }
        })
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation()
        } else {
            Toast.makeText(this, "Permissão de localização necessária", Toast.LENGTH_LONG).show()
        }
    }
}