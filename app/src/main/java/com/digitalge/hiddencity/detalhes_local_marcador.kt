package com.digitalge.hiddencity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digitalge.hiddencity.Adapter.CommentsAdapter
import com.digitalge.hiddencity.Base_de_Dados.Comentarios
import com.digitalge.hiddencity.Base_de_Dados.Locais
import com.digitalge.hiddencity.databinding.ActivityDetalhesLocalMarcadorBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class detalhes_local_marcador : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesLocalMarcadorBinding
    private var lastKnownLocation: Location? = null
    private lateinit var adapter: CommentsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesLocalMarcadorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val placeID = intent.getIntExtra("localId", -1)
        if (placeID == -1) {
            Toast.makeText(this, "ID do local inválido.", Toast.LENGTH_SHORT).show()
            finish()  // Fecha a atividade se não houver um ID válido
            return
        }



        setupRecyclerView()
        toggleDescriptionVisibility()
        setupLocationUpdates()
        loadLocalData(placeID)
    }


    private fun fetchComments(placeID: String) {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val comments = withContext(Dispatchers.IO) {
                db.ComentariosDao().buscarComentariosPorPlaceId(placeID)
            }
            withContext(Dispatchers.Main) {
                adapter.updateData(comments)
            }
        }
    }

    private fun loadLocalData(placeID: Int) {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val local = withContext(Dispatchers.IO) {
                db.LocaisDao().buscarLocalPorId(placeID)
            }
            local?.let {
                runOnUiThread {
                    updateUI(it, lastKnownLocation)
                    fetchComments(placeID.toString())
                }
            } ?: run {
                Toast.makeText(
                    this@detalhes_local_marcador,
                    "Local não encontrado",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun updateUI(local: Locais, lastKnownLocation: Location?) {
        with(binding) {
            localNameTextView.text = local.Nome
            localDescriptionTextView.text = local.Descricao
            localRatingTextView.text = formatRating(local.Avalicao)
            creatorNameTextView.text = local.nome_uti  // Supondo que há um campo nomeUtilizador no Local
            // Carrega a imagem se necessário
            Glide.with(this@detalhes_local_marcador)
                .load(local.Imagens)
                .error(R.drawable.ic_launcher_background)
                .into(localImageView)

            lastKnownLocation?.let {
                val results = FloatArray(1)
                Location.distanceBetween(it.latitude, it.longitude, local.Latitude, local.Longitude, results)
                localDistanceTextView.text = formatDistance(results[0])
            } ?: run {
                localDistanceTextView.text = "Distância não disponível"
            }

        }
    }


    private fun setupLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let {
                    lastKnownLocation = it
                    Log.d("Location Update", "New location: ${it.latitude}, ${it.longitude}")
                }
            }
        }, Looper.getMainLooper())
    }

    private fun formatDistance(distanceInMeters: Float): String {
        return if (distanceInMeters < 1000) {
            String.format("%d m", distanceInMeters.toInt())
        } else {
            String.format("%.1f km", distanceInMeters / 1000)
        }
    }

    private fun formatRating(rating: Float): String {
        return String.format("%.1f", rating)
    }

    private fun toggleDescriptionVisibility() {
        binding.backButton.setOnClickListener { onBackPressed() }

        binding.submitButton.setOnClickListener { submitComment() }

       binding.localCommentsTextView.setOnClickListener {
           // Torna o RecyclerView de comentários visível
           binding.commentsRecyclerView.visibility = View.VISIBLE
           binding.ratingBar.visibility = View.VISIBLE
           binding.commentInput.visibility = View.VISIBLE
           binding.Botoes.visibility = View.VISIBLE

           val placeId = intent.getStringExtra("place_id")
           Log.d("DetalhesLocalActivity", "placeID recebido: $placeId")

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
   }

    private fun submitComment(){
        val commentDescription = binding.commentInput.text.toString().trim()
        val rating = binding.ratingBar.rating.toDouble()
        val userID = getUserId()
        val userName = getLoggedInUserName()
        val placeIDInt = intent.getIntExtra("localId", -1)
        if (placeIDInt == -1) {
            Toast.makeText(this, "ID do local inválido.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val placeID = placeIDInt.toString()


        if (commentDescription.isEmpty()){
            Toast.makeText(this, "O comentário não pode estar vazio.", Toast.LENGTH_SHORT).show()
            return
        }

        val comentario = Comentarios(
            Descricao = commentDescription,
            Avalicao = rating,
            IdUtilizador = userID,
            Nome = userName,
            PlaceID = placeID
        )

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@detalhes_local_marcador)
            db.ComentariosDao().inserirComentario(comentario)
            Toast.makeText(this@detalhes_local_marcador, "Comentário enviado com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.commentsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val loggedInUserId = getUserId()
        val onCommentClick: (Comentarios, Boolean) -> Unit = { comentario, isSameUser ->
            if (isSameUser) {
                openFragmentInMainActivity("Contas")
            } else {
                openFragmentInMainActivity("Conta_publica")
            }
        }
        // Inicializando o adapter com todos os parâmetros necessários
        adapter = CommentsAdapter(mutableListOf(), this, loggedInUserId, onCommentClick)

        recyclerView.adapter = adapter
    }

    private fun openFragmentInMainActivity(fragmentTag: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("OPEN_FRAGMENT", fragmentTag)
        }
        startActivity(intent)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    private fun getUserId(): Int {
        return getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("UteID", -1)
    }

    fun getLoggedInUserName(): String {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPref.getString("UteNome", "Utilizador Desconhecido") ?: "Utilizador Desconhecido"
    }

}
