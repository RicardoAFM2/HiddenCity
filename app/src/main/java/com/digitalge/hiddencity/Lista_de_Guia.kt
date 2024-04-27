package com.digitalge.hiddencity



import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Adapter.GuiaAdapter
import com.digitalge.hiddencity.Base_de_Dados.Guia
import com.digitalge.hiddencity.databinding.DialogAddItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Lista_de_Guia: Fragment() {


    private lateinit var pickImageResultLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var guiaAdapter: GuiaAdapter
    private lateinit var trashButton: ImageButton
    private var guias = mutableListOf<Guia>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_lista_de_guia, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewGuia)
        recyclerView.layoutManager = LinearLayoutManager(context)
        guiaAdapter = GuiaAdapter(emptyList()) {
            setupRecyclerView()
        }



        recyclerView.adapter = guiaAdapter

        trashButton = view.findViewById(R.id.trash_icon)
        val addButton = view.findViewById<View>(R.id.mais)
        trashButton.visibility = View.INVISIBLE

        trashButton.setOnClickListener {
            guias.firstOrNull()?.let { guia ->  confirmDeletion(guia)}
        }

        addButton.setOnClickListener { showAddItemDialog() }
        setupImagePicker()
        loadDataFromDatabase()

        return view
    }

    private fun deleteGuia(guia: Guia) {
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(requireContext()).GuiaDao().Eliminar(guia)
            withContext(Dispatchers.Main){
                guias.remove(guia)
                guiaAdapter.notifyDataSetChanged()
            }
        }
    }



    private fun loadDataFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val guiaList = AppDatabase.getDatabase(requireContext()).GuiaDao().buscarTodosGuia()
            withContext(Dispatchers.Main) {
                // Make sure to update the adapter's data on the main thread
                guiaAdapter.guias = guiaList
                guiaAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun setupRecyclerView() {
        val guiaAdapter = GuiaAdapter(guias) { guia ->
            // Show the trash button when an item is long-pressed
            trashButton.visibility = View.VISIBLE
            trashButton.setOnClickListener { confirmDeletion(guia) } }
        recyclerView.adapter = guiaAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun confirmDeletion(guia: Guia) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar para eliminar")
            .setMessage("Tem a certeza de que pretende apagar este guia?")
            .setPositiveButton("Eliminar") {dialog, which ->
                deleteGuia(guia)
                trashButton.visibility = View.INVISIBLE
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    //Função para abrir o dialogo para criar um novo guia
    private fun showAddItemDialog() {
        val binding = DialogAddItemBinding.inflate(layoutInflater)
        val dialogView = binding.root

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Adicionar Novo Guia")
            .setPositiveButton("Guardar") {dialog, which ->
                val NomeDoGuia = binding.NomeDoGuia.text.toString()
                val isPublico = if(binding.switchPublicPrivate.isChecked ) 1 else 0
                val userId = getUserId()
                salvarGuia(NomeDoGuia, isPublico, userId, selectedImageUri ?: "" )
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
        binding.btnChooseImage.setOnClickListener { openGalleryForImage() }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageResultLauncher.launch(intent)
    }

    private fun setupImagePicker(){
        pickImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                selectedImageUri  = result.data?.data.toString()
            }
        }
    }

    private fun salvarGuia(nome: String, publico: Int, userId: Int,url: String) {
        val guia = Guia(Nome = nome, publico = publico, IdUtilizador = userId, url = url)
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(requireContext()).GuiaDao().inserirGuia(guia)
        }
    }
    private fun getUserId(): Int {
        return requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("UteID", -1)
    }

}


