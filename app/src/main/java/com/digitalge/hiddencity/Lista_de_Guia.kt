package com.digitalge.hiddencity


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
    companion object {
        // Um código de solicitação inteiro único que você define.
        const val PERMISSIONS_REQUEST_READ_STORAGE  = 101
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista_de_guia, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewGuia)



        recyclerView = view.findViewById(R.id.recyclerViewGuia)
        trashButton = view.findViewById(R.id.trash_icon)
        trashButton.visibility = View.INVISIBLE

        val layoutRoot: RelativeLayout = view.findViewById(R.id.layoutRoot)
        layoutRoot.setOnTouchListener { v, _ ->
            if (v !is RecyclerView) {
                val trashButton: ImageButton = view.findViewById(R.id.trash_icon)
                trashButton.visibility = View.GONE
                val firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                // Itera sobre as posições dos itens visíveis e limpa as animações.
                for (position in firstVisibleItemPosition..lastVisibleItemPosition) {
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                    guiaAdapter.clearItemAnimations(viewHolder)
                }
            }
            true
        }


        guiaAdapter = GuiaAdapter(
            guias = guias,
            onItemLongClicked = { guiaSelecionado ->
                // Ação para o clique longo - Mostra o botão de lixo
                trashButton.visibility = View.VISIBLE
                trashButton.setOnClickListener {
                    confirmDeletion(guiaSelecionado)
                }
            },
            onItemClicked = { guiaSelecionado ->
                // Ação para o clique simples - Abre a nova atividade
                val intent = Intent(context, Guia_cont::class.java)
                intent.putExtra("NOME_DA_GUIA", guiaSelecionado.Nome)
                intent.putExtra("NOME_DO_CRIADOR", guiaSelecionado.IdGuia)
                startActivity(intent)
            }
        )


        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = guiaAdapter



        val addButton = view.findViewById<View>(R.id.mais)


        setupRecyclerView()
        addButton.setOnClickListener { showAddItemDialog() }
        setupImagePicker()
        loadDataFromDatabase()


        return view
    }



    private fun deleteGuia(guia: Guia) {
        CoroutineScope(Dispatchers.IO).launch {
            // Deleta o guia do banco de dados
            AppDatabase.getDatabase(requireContext()).GuiaDao().Eliminar(guia)
            withContext(Dispatchers.Main) {
                // Encontra o índice do guia na lista antes de remover
                guias.indexOf(guia).let { index ->
                    if (index != -1) {
                        guias.removeAt(index)
                        guiaAdapter.notifyItemRemoved(index)
                        // Após remover o item, também é necessário chamar notifyItemRangeChanged
                        guiaAdapter.notifyItemRangeChanged(index, guias.size)
                    }
                }
            }
        }
    }



    private fun loadDataFromDatabase() {
        val userId = getUserId()
        CoroutineScope(Dispatchers.IO).launch {
            val guiaList = AppDatabase.getDatabase(requireContext()).GuiaDao().buscarGuiaPorUtilizadorId(userId)
            withContext(Dispatchers.Main) {
                guias.clear()
                guias.addAll(guiaList)
                guiaAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun setupRecyclerView() {
        guiaAdapter = GuiaAdapter(
            guias = guias,
            onItemLongClicked = { guia ->
                // Ação de clique longo
                trashButton.visibility = View.VISIBLE
                trashButton.setOnClickListener { confirmDeletion(guia) }
            },
            onItemClicked = { guia ->
                val intent = Intent(context, Guia_cont::class.java)
                intent.putExtra("NOME_DA_GUIA", guia.Nome)
                intent.putExtra("NOME_DO_CRIADOR", guia.IdGuia)
                startActivity(intent)
            }
        )
        recyclerView.adapter = guiaAdapter
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
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_READ_STORAGE
            )
        } else {
            // Se a permissão já foi concedida, abra a galeria
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            pickImageResultLauncher.launch(intent)
        }
    }

    private fun setupImagePicker() {
        pickImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                selectedImageUri = result.data?.data.toString()
                Log.d("Lista_de_Guia", "Selected image URI: $selectedImageUri")
            }
        }
    }

    private fun salvarGuia(nome: String, publico: Int, userId: Int, imageUrl: String) {
        val novoGuia = Guia(Nome = nome, publico = publico, IdUtilizador = userId, url = imageUrl)
        CoroutineScope(Dispatchers.IO).launch {
            val id = AppDatabase.getDatabase(requireContext()).GuiaDao().inserirGuia(novoGuia)
            withContext(Dispatchers.Main) {
                val guiaComId = novoGuia.copy(IdGuia = id.toInt()) // Assumindo que o ID é autoincrement
                guias.add(guiaComId)
                guiaAdapter.notifyItemInserted(guias.size - 1)
            }
        }
    }
    private fun getUserId(): Int {
        return requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("UteID", -1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openGalleryForImage()
                } else {
                    Toast.makeText(context, "Permissão para acessar o armazenamento foi negada", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}


