package com.digitalge.hiddencity


import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.digitalge.hiddencity.Adapter.PlaceInfo
import com.digitalge.hiddencity.Adapter.PlacesAdapter
import com.digitalge.hiddencity.Imagens.ImageSliderAdapter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient


class Home : Fragment(R.layout.fragment_home) {
    private lateinit var viewPager: ViewPager2
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: ImageSliderAdapter
    private var images: MutableList<Bitmap> = mutableListOf()
    private var placeIds: MutableList<String> = mutableListOf()







    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchInput: EditText = view.findViewById(R.id.search_input)
        val placesRecyclerView: RecyclerView = view.findViewById(R.id.places_recycler_view)



        val placesAdapter = PlacesAdapter(emptyList(), this::onPlaceClicked)
        placesRecyclerView.adapter = placesAdapter
        placesRecyclerView.layoutManager = LinearLayoutManager(context)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    placesRecyclerView.visibility = View.GONE // Oculta o RecyclerView quando não há texto
                } else {
                    placesRecyclerView.visibility = View.VISIBLE // Mostra o RecyclerView quando há texto
                    searchPlaces(s.toString(), placesAdapter)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        placesRecyclerView.visibility = View.GONE

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


    private fun searchPlaces(query: String, adapter: PlacesAdapter) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val places = response.autocompletePredictions.map {
                PlaceInfo(it.getPrimaryText(null).toString(), it.placeId)
            }
            adapter.updateData(places)
        }.addOnFailureListener { e ->
            Log.e("API_ERROR", "Error fetching places: ${e.message}")
        }
    }

    private fun onPlaceClicked(placeInfo: PlaceInfo) {
        // Ação ao clicar em um lugar, pode abrir uma nova Activity com detalhes do lugar
        val intent = Intent(activity, DetalhesLocalActivity::class.java)
        intent.putExtra("place_id", placeInfo.id)
        startActivity(intent)
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
                val intent = Intent(activity, hoteis::class.java)
                startActivity(intent)

            }
        }

        val RestaurantesLayout = view?.findViewById<LinearLayout>(R.id.Restaurantes)
        if (RestaurantesLayout != null) {
            RestaurantesLayout.setOnClickListener {
                // Iniciar a Activity que mostra os detalhes dos monumentos
                val intent = Intent(activity, Restaurantes::class.java)
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

    override fun onResume() {
        super.onResume()
        // Limpa o campo de busca e oculta o RecyclerView quando o fragmento é retomado
        val searchInput: EditText = view?.findViewById(R.id.search_input) ?: return
        val placesRecyclerView: RecyclerView = view?.findViewById(R.id.places_recycler_view) ?: return

        searchInput.text.clear() // Limpa o texto
        placesRecyclerView.visibility = View.GONE // Oculta o RecyclerView
    }
}