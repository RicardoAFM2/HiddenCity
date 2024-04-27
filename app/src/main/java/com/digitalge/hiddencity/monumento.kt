package com.digitalge.hiddencity


import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class monumento : AppCompatActivity() {
    private lateinit var placesClient: PlacesClient
    private lateinit var gridLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monumento)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk")
        }
        placesClient = Places.createClient(this)

        loadMonumentsData()
    }

    private fun loadMonumentsData() {
        // Crie uma lista de IDs dos lugares ou uma busca por lugares do tipo 'monument'
        val monumentPlaceIds = listOf("PLACE_ID_1", "PLACE_ID_2", "PLACE_ID_3", "PLACE_ID_4")
        monumentPlaceIds.forEachIndexed { index, placeId ->
            val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.RATING, Place.Field.PHOTO_METADATAS)
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                // Assumindo que você tem um método `updateMonumentLayout` para atualizar a UI:
                updateMonumentLayout(place, index + 1) // index + 1 porque seus IDs começam de 1
            }
        }
    }

    private fun updateMonumentLayout(place: Place, monumentIndex: Int) {
        val monumentLayoutId = resources.getIdentifier("Monumento_$monumentIndex", "id", packageName)
        val monumentLayout = findViewById<LinearLayout>(monumentLayoutId)

        val imageView = monumentLayout.getChildAt(0) as ImageView
        val textView = monumentLayout.getChildAt(1) as TextView

        // Atualize o nome do monumento
        textView.text = place.name

        // Se tiver metadados de foto, carregue a imagem
        place.photoMetadatas?.firstOrNull()?.let { photoMetadata ->
            // Assumindo que você tem um método `fetchAndSetImage` para carregar e definir a imagem:
            fetchAndSetImage(photoMetadata, imageView)
        }
    }

    // Suponha que você tem um método `fetchAndSetImage` para fazer o download da imagem
    private fun fetchAndSetImage(photoMetadata: PhotoMetadata, imageView: ImageView) {
        // ... Código para carregar a imagem usando o metadado de foto e Places API
    }
}