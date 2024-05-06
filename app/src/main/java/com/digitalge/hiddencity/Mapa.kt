package com.digitalge.hiddencity


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.digitalge.hiddencity.Base_de_Dados.Locais
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Mapa : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var selectedImageUri: Uri? = null
    private var lastPosition: LatLng? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mapa, container, false)
        MapsInitializer.initialize(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        val buttonEscolherImagem = view.findViewById<ImageView>(R.id.mais)
        buttonEscolherImagem.setOnClickListener {
            showAddPOIDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    private fun showAddPOIDialog() {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.dialog_add_poi, null)

        val editTextNome = view.findViewById<EditText>(R.id.editTextNome)
        val editTextDescricao = view.findViewById<EditText>(R.id.editTextDescricao)
        val editTextTipo = view.findViewById<EditText>(R.id.editTextTipo)
        val buttonEscolherImagem = view.findViewById<Button>(R.id.buttonEscolherImagem)
        val switchPrivacidade = view.findViewById<Switch>(R.id.switchPrivacidade)


        buttonEscolherImagem.setOnClickListener {
            openImagePicker()
        }

        AlertDialog.Builder(context)
            .setTitle("Adicionar Ponto de Interesse")
            .setView(view)
            .setPositiveButton("Criar") { dialog, which ->
                val nome = editTextNome.text.toString()
                val descricao = editTextDescricao.text.toString()
                val tipo = editTextTipo.text.toString()
                val isPrivate = switchPrivacidade.isChecked
                // Salvar essas informações no banco de dados ou onde necessário

                salvarLocal(nome, descricao, tipo, isPrivate, selectedImageUri)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }
    fun getLoggedInUserName(): String {
        val sharedPref = requireContext().getSharedPreferences("AppPrefs", AppCompatActivity.MODE_PRIVATE)
        return sharedPref.getString("UteNome", "Utilizador Desconhecido") ?: "Utilizador Desconhecido"
    }


    private fun salvarLocal(nome: String, descricao: String, tipo: String, isPrivate: Boolean, imagemUri: Uri?) {
        if (lastPosition == null) {
            Toast.makeText(context, "Localização não disponível, tente novamente.", Toast.LENGTH_SHORT).show()
            return
        }

        val nome_do_uti = getLoggedInUserName()

        val local = Locais(
            Nome = nome,
            Descricao = descricao,
            Latitude = lastPosition!!.latitude,
            Longitude = lastPosition!!.longitude,
            Avalicao = 0f,  // Inicializar como 0 ou obter de outra forma
            Imagens = imagemUri.toString(),  // Converter Uri para String
            Tipo = tipo,
            nome_uti = nome_do_uti,  // Este deve ser obtido do contexto de login/usuario
            privacidade = if (isPrivate) 1 else 0
        )

        lifecycleScope.launch {
            // Supondo que você tem uma função para inserir no banco
            val db = AppDatabase.getDatabase(requireContext())
            db.LocaisDao().inserir(local)
            Toast.makeText(context, "Local salvo com sucesso!", Toast.LENGTH_SHORT).show()
            loadLocaisFromDatabase()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedImageUri = data?.data
        }
    }

    private fun loadLocaisFromDatabase() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val locais = db.LocaisDao().buscarTodosLocais() // Supõe que há um método para buscar todos os locais
            withContext(Dispatchers.Main) {
                locais.forEach { local ->
                    val latLng = LatLng(local.Latitude, local.Longitude)
                    val marker = mMap.addMarker(MarkerOptions()
                        .position(latLng)
                        .title(local.Nome)
                        .snippet(local.Descricao))
                    marker?.tag = local.IdLocais
                    // Você pode adicionar um ícone personalizado, se necessário, com .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = true
        enableMyLocation()
        loadLocaisFromDatabase()



        mMap.setOnPoiClickListener { poi ->
            val intent = Intent(context, DetalhesLocalActivity::class.java).apply {
                // Passar o ID do local (placeId) para a DetalhesLocalActivity
                putExtra("place_id", poi.placeId)
                // Passar o nome do local (name) para a DetalhesLocalActivity
                putExtra("place_name", poi.name)
            }
            startActivity(intent)
        }

        setupMarkerClickListener()
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun setupMarkerClickListener() {
        mMap.setOnMarkerClickListener { marker ->
            val localId = marker.tag as? Int
            if (localId != null) {
                Log.d("Mapa", "Clique no marcador com ID: $localId")
                openDetailPage(localId)
            } else {
                Log.d("Mapa", "Clique no marcador sem ID associado.")
            }
            true
        }
    }

    private fun openDetailPage(localId: Int) {
        val intent = Intent(context, detalhes_local_marcador::class.java).apply {
            putExtra("localId", localId)
        }
        startActivity(intent)
    }


    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled = true
        setupLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            var lastPosition: LatLng? = null  // Armazena a última posição conhecida

            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    if (lastPosition == null || distance(lastPosition!!, currentLatLng) > 10) {  // 50 metros de diferença
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f), 2000, null)
                        updateLocation(location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, null)
    }

    private fun updateLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        // Atualiza a variável lastPosition
        lastPosition = currentLatLng
        // Aqui você pode mover a câmera, se necessário
    }

    // Função para calcular a distância entre duas coordenadas em metros
    private fun distance(start: LatLng, end: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude,
            start.longitude,
            end.latitude,
            end.longitude,
            results
        )
        return results[0]
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val IMAGE_PICK_CODE = 1000
    }

}


