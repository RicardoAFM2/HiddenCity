package com.digitalge.hiddencity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.digitalge.hiddencity.Adapter.CommentsAdapter
import com.digitalge.hiddencity.Base_de_Dados.Comentarios
import com.digitalge.hiddencity.Base_de_Dados.Favoritos
import com.digitalge.hiddencity.Dao.ComentariosDao
import com.digitalge.hiddencity.databinding.ActivityDetalhesLocalBinding
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
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
    private lateinit var binding: ActivityDetalhesLocalBinding
    private var userRating: Float = 0f
    private lateinit var adapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesLocalBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk")
        }
        placesClient = Places.createClient(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val placeID = intent.getStringExtra("place_id")
        Log.d("DetalhesLocalActivity", "placeID recebido: $placeID")

        fetchPlaceDetails(placeID)
        clicarimagem()
        setupFavoriteButton()
        toggleDescriptionVisibility()
        setupRatingBar()
        fetchComments()
        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CommentsAdapter(mutableListOf())
        binding.commentsRecyclerView.adapter = adapter

    }
    private fun fetchComments() {
        val placeId = intent.getStringExtra("place_id") ?: return Toast.makeText(this, "ID do local não disponível", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            val comentarios = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(applicationContext).ComentariosDao().buscarComentariosPorPlaceId(placeId)
            }
            withContext(Dispatchers.Main) {
                if (comentarios.isNotEmpty()) {
                    adapter.updateData(comentarios)
                } else {
                    Toast.makeText(this@DetalhesLocalActivity, "Nenhum comentário disponível para este local.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun toggleDescriptionVisibility() {
        binding.localCommentsTextView.setOnClickListener {
            // Torna o RecyclerView de comentários visível
            binding.commentsRecyclerView.visibility = View.VISIBLE
            binding.ratingBar.visibility = View.VISIBLE
            binding.commentInput.visibility = View.VISIBLE
            binding.Botoes.visibility = View.VISIBLE

            val placeId = intent.getStringExtra("place_id")
            Log.d("DetalhesLocalActivity", "placeID recebido: $placeId")

            if (placeId != null) {
                fetchComments()
            } else {
                Toast.makeText(this, "ID do local não está disponível.", Toast.LENGTH_SHORT).show()
            }

            // Esconde a descrição para evitar sobreposição de conteúdo
            binding.localDescriptionTextView.visibility = View.GONE
        }

        binding.textView.setOnClickListener {
            // Mostrar a descrição e esconder ratingBar e commentInput
            binding.localDescriptionTextView.visibility = View.VISIBLE
            binding.ratingBar.visibility = View.GONE
            binding.Botoes.visibility = View.GONE
            binding.commentInput.visibility = View.GONE
            binding.commentsRecyclerView.visibility = View.GONE
        }

        binding.submitButton.setOnClickListener { GuardarComentario() }
    }

    private fun showCommentsForPlace(placeId: String) {
        val comentariosDao = AppDatabase.getDatabase(applicationContext).ComentariosDao()
        lifecycleScope.launch {
            val comentarios = comentariosDao.buscarComentariosPorPlaceId(placeId)
            withContext(Dispatchers.Main) {
                displayComments(comentarios)
            }
        }
    }

    private fun displayComments(comentarios: List<Comentarios>) {
        binding.commentsRecyclerView.removeAllViews()

        comentarios.forEach { comentario ->
            val commentView = TextView(this).apply {
                text = "${comentario.Nome}: ${comentario.Descricao} - Avaliação: ${comentario.Avalicao}"
                // Defina outros estilos como necessário aqui
            }
            binding.commentsRecyclerView.addView(commentView)
        }
    }

    private fun setupRatingBar() {
        binding.ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                userRating = rating // Atualiza a variável de classe com a nova avaliação
            }
        }
    }

    private fun GuardarComentario() {
        val Nome = getLoggedInUserName()
        val descricaoComentario = binding.commentInput.text.toString()
        val avaliacao = userRating.toDouble()
        val idUtilizador = getUserId()

        val placeIdValuecom = intent.getStringExtra("place_id")
        if (placeIdValuecom == null) {
            Toast.makeText(
                applicationContext,
                "Erro: ID do local não está disponível.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }


        val novoComentario = Comentarios(
            PlaceID = placeIdValuecom,
            Nome = Nome,
            Descricao = descricaoComentario,
            Avalicao = avaliacao,
            IdUtilizador = idUtilizador
        )
        val comentariosDao = AppDatabase.getDatabase(applicationContext).ComentariosDao()
        lifecycleScope.launch {
            val resultadoInsercao = comentariosDao.inserirComentario(novoComentario)
            withContext(Dispatchers.Main) {
                if (resultadoInsercao > 0) {
                    // Comentário salvo com sucesso, agora limpe a RatingBar e o campo de texto.
                    binding.ratingBar.rating = 0f // Reseta a RatingBar
                    binding.commentInput.text.clear() // Limpa o campo de comentário
                    Toast.makeText(
                        applicationContext,
                        "Comentário salvo com sucesso.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Houve um erro ao salvar o comentário.
                    Toast.makeText(
                        applicationContext,
                        "Erro ao salvar o comentário.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun clicarimagem(){
        binding.backButton.setOnClickListener { onBackPressed() }

    }

    fun getLoggedInUserName(): String {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPref.getString("UteNome", "Utilizador Desconhecido") ?: "Utilizador Desconhecido"
    }

    private fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(applicationContext, AppDatabase::class.java, "hiddencity.db").build()
    }

    private fun getUserId(): Int {
        return getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("UteID", -1)
    }

    private fun setupFavoriteButton(){
        val idUtilizador = getUserId()
        binding.favoriteButton.setOnClickListener {
            it.isSelected = !it.isSelected
            val placeIdValue = intent.getStringExtra("place_id")
            if (placeIdValue == null) {
                Toast.makeText(applicationContext, "Erro: ID do local não está disponível.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageUrl = if (binding.localImageView.tag != null && binding.localImageView.tag.toString().isNotEmpty()) {
                binding.localImageView.tag.toString()
            } else {
                "URL_da_imagem_padrao" // Substitua por uma URL de imagem padrão ou caminho de recurso
            }

            lifecycleScope.launch {
                val favoritos = Favoritos(
                    Nome = binding.localNameTextView.text.toString(),
                    PlaceID = placeIdValue,
                    URL = imageUrl,
                    IdUtilizador = idUtilizador
                )
                try {
                    getDatabase().FavoritosDao().inserirFavoritos(favoritos)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Local adicionado aos favoritos!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Erro ao adicionar aos favoritos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun fetchPlaceDetails(placeId: String?){
        if (placeId == null) {
            Toast.makeText(this, "ID do local não foi fornecido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        checkLocationPermissionAndFetch()

        val placeFields = listOf(
            Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS,
            Place.Field.LAT_LNG, Place.Field.RATING
        )

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            updateUIWithPlaceDetails(place)
        }.addOnFailureListener { exception ->
            Log.e("API Error", "Erro ao buscar detalhes do local: ${exception.message}")
        }
    }


    private fun updateUIWithPlaceDetails(place: Place) {
        binding.localNameTextView.text = place.name

        // Buscar a localização atual do usuário e calcular a distância
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
            location?.let { currentLocation ->
                val distanceInMeters = SphericalUtil.computeDistanceBetween(
                    LatLng(currentLocation.latitude, currentLocation.longitude),
                    place.latLng
                )
                val distanceInKm = distanceInMeters / 1000.0
                binding.localDistanceTextView.text = getString(R.string.distance_text, distanceInKm)
            }
        }

        // Exibir a classificação
        place.rating?.let { rating ->
            binding.localRatingTextView.text = getString(R.string.rating_text, rating)
        }

        // Carregar a foto do local
        loadPhoto(place.photoMetadatas)

        // Buscar e exibir a descrição da Wikipedia
        place.name?.let { name ->
            fetchSummaryFromWikipedia(name)
        }
    }

    private fun loadPhoto(photoMetadata: List<PhotoMetadata>?) {
        photoMetadata?.firstOrNull()?.let {
            val photoRequest = FetchPhotoRequest.builder(it)
                .setMaxWidth(resources.getDimensionPixelSize(R.dimen.default_image_width))
                .setMaxHeight(resources.getDimensionPixelSize(R.dimen.default_image_height))
                .build()

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                val bitmap = fetchPhotoResponse.bitmap
                binding.localImageView.setImageBitmap(bitmap)
            }.addOnFailureListener { exception ->
                // Handle the error
                Log.e("DetalhesLocalActivity", "Photo not found: ${exception.message}")
            }
        }
    }

    private fun fetchSummaryFromWikipedia(placeName: String) {
        val formattedName = placeName.replace(" ", "_")
        val url = "https://pt.wikipedia.org/api/rest_v1/page/summary/$formattedName"

        val request = Request.Builder().url(url).build()
        OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle the error
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let {
                    val jsonObject = JSONObject(it)
                    val extract = jsonObject.optString("extract", "Summary not available.")
                    runOnUiThread {
                        binding.localDescriptionTextView.text = extract
                    }
                } ?: runOnUiThread {
                    binding.localDescriptionTextView.text = "Summary not available."
                }
            }
        })
    }

    private fun checkLocationPermissionAndFetch() {
        // Implementação para verificar permissão e buscar localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            return
        }
        fetchCurrentLocation()
    }

    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  // Retorna se ainda não houver permissão; isso é apenas uma precaução.
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                updateUIWithLocation(location)
            } else {
                Toast.makeText(this, "Unable to get current location.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUIWithLocation(location: Location) {
        Toast.makeText(this, "Location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissão concedida.
                    fetchCurrentLocation()
                } else {
                    // Permissão negada.
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}