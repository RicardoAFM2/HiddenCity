package com.digitalge.hiddencity


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Adapter.PlaceItem
import com.digitalge.hiddencity.Adapter.PlacesAdapter
import com.digitalge.hiddencity.Adapter.SimpleAdapter
import com.digitalge.hiddencity.Base_de_Dados.Guia
import com.digitalge.hiddencity.Base_de_Dados.Guia_e_Locais
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Guia_cont : AppCompatActivity() {

    private var currentSelectedPlace: Pair<String, String>? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SimpleAdapter
    private lateinit var placesClient: PlacesClient
    private lateinit var trashIcon: ImageView
    private var currentGuiaELocais: Guia_e_Locais? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guia_cont)

        val nomeDaGuia = intent.getStringExtra("NOME_DA_GUIA")
        val guiaId = intent.getIntExtra("NOME_DO_CRIADOR", -1)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk")
        }
        placesClient = Places.createClient(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewGuia)
        adapter = SimpleAdapter(
            mutableListOf(),
            this::onPlaceItemClick,  // Trata clique normal
            this::onPlaceItemLongClick,  // Trata clique longo
            placesClient
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Encontrar as TextViews e configurar os textos
        val textViewGuia = findViewById<TextView>(R.id.textView9)
        val textViewCriador = findViewById<TextView>(R.id.creatorName)

        textViewGuia.text = nomeDaGuia ?: "Nome da Guia"
        textViewCriador.text = getLoggedInUserName()

        findViewById<View>(R.id.mais).setOnClickListener { showDialogWithRecyclerView() }
        findViewById<View>(R.id.voltar).setOnClickListener { onBackPressed() }

        trashIcon = findViewById(R.id.trashIcon)
        trashIcon = findViewById<ImageView>(R.id.trashIcon).apply {
            setOnClickListener {
                Log.d("Guia_cont", "Lixeira clicada")
                currentGuiaELocais?.let {
                    confirmDeletion(it)
                } ?: Toast.makeText(this@Guia_cont, "Nenhum item selecionado", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        val layoutRoot = findViewById<View>(R.id.layoutRoot)
        layoutRoot.setOnClickListener {
            adapter.clearAnimations()
            trashIcon.visibility = View.GONE
        }

        val shouldHideAddButton = intent.getBooleanExtra("HIDE_ADD_BUTTON", false)
        if (shouldHideAddButton) {
            val addButton = findViewById<View>(R.id.mais)  // Certifique-se de ter o ID correto
            addButton.visibility = View.GONE
        }

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
            }
        })


        setupRecyclerView()
        fetchDataFromDatabase(guiaId)
    }


    private fun showDialogWithRecyclerView() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_recycler_view, null)
        val searchEditText = dialogView.findViewById<EditText>(R.id.searchEditText)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.searchRecyclerView)

        val adapter = SimpleAdapter(
            mutableListOf(),
            { selectedItem ->  // OnItemClick
                searchEditText.setText(selectedItem.name)
                currentSelectedPlace = Pair(selectedItem.name, selectedItem.placeId)
            },
            { selectedItem ->  // OnItemLongClick
                // Aqui você pode definir o que fazer quando o item é pressionado longamente
            },
            placesClient
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    searchPlaces(query, adapter)
                }
            }
        })

        AlertDialog.Builder(this)
            .setTitle("Pesquisar locais")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, which ->
                if (currentSelectedPlace != null) {
                    savePlaceToDatabase(currentSelectedPlace)
                }
            }
            .setNegativeButton("Cancelar", null)
            .setOnDismissListener {
                // Isto é chamado quando o diálogo é descartado
                adapter.notifyDataSetChanged()  // Força a RecyclerView a atualizar
            }
            .show()
    }

    private fun stopAllAnimationsAndHideTrashIcon() {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i)?.clearAnimation()
        }
        trashIcon.visibility = View.GONE
    }

    private fun savePlaceToDatabase(selectedPlace: Pair<String, String>?) {
        selectedPlace?.let { place ->
            // Obtenha o ID da guia de algum lugar, por exemplo, passado através da Intent
            val guiaId = intent.getIntExtra(
                "NOME_DO_CRIADOR",
                -1
            ) // -1 é um valor padrão se o ID não for encontrado

            // Crie o objeto para inserir no banco
            val newEntry = Guia_e_Locais(
                Nome = place.first,
                placeID = place.second,
                url = "",
                IdGuia = guiaId
            )

            // Inserir no banco de dados
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(applicationContext).Guia_e_LocaisDao()
                    .inserirGuia_e_Locais(newEntry)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Local adicionado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchDataFromDatabase(guiaId)  // Re-fetch the data to update the UI immediately
                }
            }
        }
    }

    private fun searchPlaces(query: String, adapter: SimpleAdapter) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val placesList = response.autocompletePredictions.map {
                PlaceItem(it.getPrimaryText(null).toString(), it.placeId)
            }
            adapter.updateData(placesList)
        }.addOnFailureListener { e ->
            Log.e("PlacesAPI", "Error finding places: ", e)
        }
    }

    private fun fetchDataFromDatabase(guiaId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            // Buscar dados do banco de dados que correspondem ao ID da guia
            val guiaseLocais = AppDatabase.getDatabase(applicationContext).Guia_e_LocaisDao()
                .buscarPorIdGuia(guiaId)

            // Filtrar dados com base no ID da guia recebido
            val filteredPlaces = guiaseLocais.filter { it.IdGuia == guiaId }

            val placeItems = filteredPlaces.map { PlaceItem(it.Nome, it.placeID, it.url) }

            withContext(Dispatchers.Main) {
                // Atualizar o adapter apenas se houver itens a mostrar
                if (placeItems.isNotEmpty()) {
                    adapter.updateData(placeItems)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Nenhum local encontrado para esta guia.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        trashIcon = findViewById(R.id.trashIcon)
        recyclerView = findViewById(R.id.recyclerViewGuia)
        adapter = SimpleAdapter(
            mutableListOf(),
            this::onPlaceItemClick,
            this::onPlaceItemLongClick,
            placesClient
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val layoutRoot = findViewById<View>(R.id.layoutRoot)
        layoutRoot.setOnClickListener {
            stopAllAnimationsAndHideTrashIcon()
        }
    }

    private fun onPlaceItemLongClick(placeItem: PlaceItem) {
        // Lançar uma corrotina na thread principal, pois a visibilidade de uma view é uma operação de UI
        CoroutineScope(Dispatchers.Main).launch {
            val guiaELocais = withContext(Dispatchers.IO) {
                val placeId =
                    placeItem.placeId  // Aqui não precisa converter para Int, supondo que o placeID seja uma String
                Log.d("Guia_cont", "Buscando no banco com placeID: $placeId")
                AppDatabase.getDatabase(applicationContext).Guia_e_LocaisDao()
                    .buscarPorPlaceID(placeId).firstOrNull()
            }

            // Verificar o resultado da consulta e ajustar a visibilidade do ícone de lixo
            if (guiaELocais != null) {
                currentGuiaELocais = guiaELocais
                trashIcon.visibility = View.VISIBLE
            } else {
                Toast.makeText(
                    this@Guia_cont,
                    "Item não encontrado no banco de dados",
                    Toast.LENGTH_SHORT
                ).show()
                trashIcon.visibility = View.GONE
            }
        }
    }

    private fun onPlaceItemClick(placeItem: PlaceItem) {
        val intent = Intent(this, DetalhesLocalActivity::class.java)
        intent.putExtra("place_id", placeItem.placeId)
        startActivity(intent)
        stopAllAnimationsAndHideTrashIcon()
    }

    fun getLoggedInUserName(): String {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPref.getString("UteNome", "Utilizador Desconhecido")
            ?: "Utilizador Desconhecido"
    }

    private fun confirmDeletion(guiaELocais: Guia_e_Locais) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar exclusão")
            .setMessage("Tem certeza que deseja excluir este item?")
            .setPositiveButton("Excluir") { dialog, which ->
                deleteItem(guiaELocais)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteItem(guiaELocais: Guia_e_Locais) {
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(applicationContext).Guia_e_LocaisDao().Eliminar(guiaELocais)
            withContext(Dispatchers.Main) {
                // Encontre o índice do item na lista antes de removê-lo
                val index = adapter.items.indexOfFirst { it.placeId == guiaELocais.placeID }
                if (index != -1) {
                    // Remove o item da lista
                    adapter.items.removeAt(index)
                    // Notifica o adapter que um item foi removido
                    adapter.notifyItemRemoved(index)
                    Toast.makeText(
                        applicationContext,
                        "Item excluído com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}



