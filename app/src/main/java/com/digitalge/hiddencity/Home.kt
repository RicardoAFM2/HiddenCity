package com.digitalge.hiddencity


import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.digitalge.hiddencity.Imagens.ImageSliderAdapter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient


class Home : Fragment(R.layout.fragment_home) {
    private lateinit var viewPager: ViewPager2
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: ImageSliderAdapter
    private var images: MutableList<Bitmap> = mutableListOf()
    private var placeIds: MutableList<String> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk")

        }

        adapter = ImageSliderAdapter(images, placeIds) { placeId ->
            // Ação de clique que passa a posição da imagem clicada
            val intent = Intent(activity, DetalhesLocalActivity::class.java)
            intent.putExtra("place_id", placeId)
            startActivity(intent)
        }


        viewPager = view.findViewById(R.id.viewPagerImages)
        viewPager.adapter = adapter
        placesClient = Places.createClient(requireContext())


        fetchPlaceImages()
        setupImageAutoRotation()
        clicar()

    }

    private fun clicar() {
        val monumentosLayout = view?.findViewById<LinearLayout>(R.id.Monumentos)
        if (monumentosLayout != null) {
            monumentosLayout.setOnClickListener {
                // Iniciar a Activity que mostra os detalhes dos monumentos
                val intent = Intent(activity, monumento::class.java)
                startActivity(intent)

            }
        }
        val MuseusLayout = view?.findViewById<LinearLayout>(R.id.Museus)
        if (MuseusLayout != null) {
            MuseusLayout.setOnClickListener {
                // Iniciar a Activity que mostra os detalhes dos monumentos
                val intent = Intent(activity, Museus::class.java)
                startActivity(intent)

            }
        }
        val HotéisLayout = view?.findViewById<LinearLayout>(R.id.Hotéis)
        if (HotéisLayout != null) {
            HotéisLayout.setOnClickListener {
                // Iniciar a Activity que mostra os detalhes dos monumentos
                val intent = Intent(activity, Museus::class.java)
                startActivity(intent)

            }
        }

        val RestaurantesLayout = view?.findViewById<LinearLayout>(R.id.Restaurantes)
        if (RestaurantesLayout != null) {
            RestaurantesLayout.setOnClickListener {
                // Iniciar a Activity que mostra os detalhes dos monumentos
                val intent = Intent(activity, Museus::class.java)
                startActivity(intent)

            }
        }
    }
    //Função para por as imagens
    private fun fetchPlaceImages() {
        val placeFields = listOf(Place.Field.PHOTO_METADATAS)

        //imagems no topo
        val placeIdsToFetch  = listOf("ChIJHW-ANATCxUcRgGI4ctqwfQM", "ChIJmQJIxlVYwokRLgeuocVOGVU")
        placeIdsToFetch.forEach { placeId ->
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                Log.d("API_SUCCESS", "Place found: ${response.place.name}")
                val place = response.place
                place.photoMetadatas?.firstOrNull()?.let { photoMetadata ->
                    val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                        val bitmap = fetchPhotoResponse.bitmap
                        images.add(bitmap) // Corretamente adicionando a imagem
                        placeIds.add(placeId) // Corretamente adicionando o placeId
                        adapter.notifyDataSetChanged()
                    }.addOnFailureListener { e ->
                        Log.e("API_ERROR", "Failed to fetch photo: ${e.message}")
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("API_ERROR", "Failed to fetch place: ${e.message}")
            }
        }
        val placesToFetch = mapOf(
            "ChIJCb_8QuJkJA0RPP5j4P4wVDo" to R.id.Monumentos_imagem,
            "ChIJD3uTd9hx5kcR1IQvGfr8dbk" to R.id.Museus_imagem,
            "ChIJkyQqpeVkJA0R9OXKcd6vwx0" to R.id.Restaurantes_imagem,
            "ChIJRbfU0jBlJA0Rn_ed48aSGm8" to R.id.Hoteis_imagem
        )

        placesToFetch.forEach { (placeId, imageViewId) ->
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                response.place.photoMetadatas?.firstOrNull()?.let { photoMetadata ->
                    val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                        updateImageView(imageViewId, fetchPhotoResponse.bitmap)
                    }.addOnFailureListener { e ->
                        Log.e("API_ERROR", "Failed to fetch photo: ${e.message}")
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("API_ERROR", "Failed to fetch place: ${e.message}")
            }
        }
    }



    private fun updateImageView(imageViewId: Int, bitmap: Bitmap) {
        val imageView = view?.findViewById<ImageView>(imageViewId)
        imageView?.setImageBitmap(bitmap)
    }



    //Função para mudar as imagens quando passa um x tempo
    private fun setupImageAutoRotation() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (viewPager.currentItem < adapter.itemCount  - 1) {
                    viewPager.currentItem = viewPager.currentItem + 1
                } else {
                    viewPager.currentItem = 0
                }
                handler.postDelayed(this, 3000) // Muda a cada 3 segundos
            }
        }
        handler.postDelayed(runnable, 3000)
    }

}

