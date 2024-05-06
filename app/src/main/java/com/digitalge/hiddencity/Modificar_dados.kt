package com.digitalge.hiddencity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.digitalge.hiddencity.Base_de_Dados.Utilizador
import com.digitalge.hiddencity.databinding.ActivityModificarDadosBinding

import kotlinx.coroutines.launch
import java.security.MessageDigest


class Modificar_dados : AppCompatActivity() {

    private lateinit var binding: ActivityModificarDadosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityModificarDadosBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        //Mudar o texto para Modificar o nome
        binding.ModificasNome.textViewItem.text = "Modificar o nome"

        //Mudar o texto para Modificar a senha
        binding.ModificasSenha.textViewItem.text = "Modificar o senha"

        //Mudar o texto para Modificar o emial
        binding.ModificasEmail.textViewItem.text = "Modificar o email"

        //Mudar o texto para Modificar o numero
        binding.ModificasNumero.textViewItem.text = "Modificar o numero"

        binding.ModificasImagem.textViewItem.text = "Modificar o Imagem"


        Clicarnaimagem()
    }

    private fun Clicarnaimagem() {
        binding.voltar.setOnClickListener {
            onBackPressed()
        }
        binding.ModificasNome.textViewItem.setOnClickListener {
            mostrarDialogoEdicaoNome()
        }

        binding.ModificasNumero.textViewItem.setOnClickListener {
            mostrarDialogoNumero()
        }

        binding.ModificasSenha.textViewItem.setOnClickListener {
            mostrarDialogoEdicaoSenha()
        }

        binding.ModificasEmail.textViewItem.setOnClickListener {
            mostrarDialogoEdicaoEmail()
        }

        binding.ModificasImagem.textViewItem.setOnClickListener {
            abrirGaleriaParaSelecionarImagem()
        }

    }

    private fun abrirGaleriaParaSelecionarImagem() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GALERIA)
    }

    companion object {
        private const val REQUEST_CODE_GALERIA = 1001
    }

    private fun mostrarDialogoEdicaoEmail() {
        val editText = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = "Digite o novo Email"
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Modificar Email")
            .setView(editText)
            .setPositiveButton("Salvar") { dialog, which ->
                val novoNome = editText.text.toString()
                val ID = getUserId()
                if (novoNome.isNotEmpty()) {
                    lifecycleScope.launch {
                        // Supondo que você tem uma variável `usuarioId` que identifica o usuário
                        try {
                            getDatabase().UtilizadorDao().atualizarEmail(ID, novoNome)
                            binding.ModificasNome.textViewItem.text = novoNome
                            Toast.makeText(
                                this@Modificar_dados,
                                "Nome atualizado!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@Modificar_dados,
                                "Erro ao atualizar o nome: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@Modificar_dados,
                        "O nome não pode ser vazio.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }


    private fun mostrarDialogoNumero() {
        val editText = EditText(this)
        editText.inputType = InputType.TYPE_CLASS_PHONE
        editText.hint = "Digite o novo número"

        AlertDialog.Builder(this)
            .setTitle("Alterar Número")
            .setView(editText)
            .setPositiveButton("Salvar") { dialog, which ->
                val novoNumero = editText.text.toString()
                if (novoNumero.isNotEmpty()) {
                    atualizarNumero(novoNumero)
                } else {
                    Toast.makeText(this, "O número não pode ser vazio.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun atualizarNumero(novoNumero: String) {
        val usuarioId = getUserId() // Suponha que você tenha esse método para obter o ID do usuário
        lifecycleScope.launch {
            try {
                getDatabase().UtilizadorDao().atualizarNumero(usuarioId, novoNumero)
                Toast.makeText(
                    this@Modificar_dados,
                    "Número atualizado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@Modificar_dados,
                    "Erro ao atualizar número: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun mostrarDialogoEdicaoSenha() {
        val editText = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Digite o novo Palavra-passe"
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Alterar  Palavra-passe")
            .setView(editText)
            .setPositiveButton("Salvar") { dialog, which ->
                val novaSenha = editText.text.toString()
                if (novaSenha.isNotEmpty()) {
                    atualizarSenha(novaSenha)
                } else {
                    Toast.makeText(this, "A senha não pode ser vazia.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    fun hashSenha(senha: String): String {
        val bytes = senha.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    private fun atualizarSenha(novaSenha: String) {
        val senhaCriptografada = hashSenha(novaSenha)
        val usuarioId = getUserId() // Suponha que você tenha esse método para obter o ID do usuário
        lifecycleScope.launch {
            try {
                getDatabase().UtilizadorDao().atualizarPalavrapasse(usuarioId, senhaCriptografada)
                Toast.makeText(
                    this@Modificar_dados,
                    "Senha atualizada com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@Modificar_dados,
                    "Erro ao atualizar senha: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun mostrarDialogoEdicaoNome() {
        val editText = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = "Digite o novo nome"
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Modificar Nome")
            .setView(editText)
            .setPositiveButton("Salvar") { dialog, which ->
                val novoNome = editText.text.toString()
                val ID = getUserId()
                if (novoNome.isNotEmpty()) {
                    lifecycleScope.launch {
                        // Supondo que você tem uma variável `usuarioId` que identifica o usuário
                        try {
                            getDatabase().UtilizadorDao().atualizarNome(ID, novoNome)
                            binding.ModificasNome.textViewItem.text = novoNome
                            Toast.makeText(
                                this@Modificar_dados,
                                "Nome atualizado!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@Modificar_dados,
                                "Erro ao atualizar o nome: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@Modificar_dados,
                        "O nome não pode ser vazio.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }


    fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(applicationContext, AppDatabase::class.java, "hiddencity.db")
            .build()
    }

    private fun getUserId(): Int {
        return getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("UteID", -1)
    }

    fun getPathFromUri(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri!!, proj, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_GALERIA && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data  // URI da imagem selecionada
            val imagePath = getPathFromUri(selectedImageUri)

            imagePath?.let {
                val userId = getUserId()  // Supõe que você tenha esse método para obter o ID do usuário
                updateImageUrlInDatabase(userId, it)
            }
        }
    }

    private fun updateImageUrlInDatabase(userId: Int, imageUrl: String) {
        lifecycleScope.launch {
            try {
                getDatabase().UtilizadorDao().atualizarImagemUrl(userId, imageUrl)
                Toast.makeText(this@Modificar_dados, "Imagem atualizada com sucesso!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@Modificar_dados, "Erro ao atualizar a imagem: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}