package com.digitalge.hiddencity

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.digitalge.hiddencity.Base_de_Dados.Utilizador
import com.digitalge.hiddencity.databinding.ActivityModificarDadosBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

       // Clicarnaimagem()


    }

    /*private fun Clicarnaimagem(){
        binding.voltar.setOnClickListener {
            onBackPressed()
        }

        binding.ModificasNome.root.setOnClickListener {
            // Supondo que 'usuarioAtual' é o seu objeto Utilizador atual.
            // Você precisará buscar essa informação do banco de dados.
            mostrarDialogoDeEdicao("Modificar o nome", usuarioAtual.nome, InputType.TYPE_CLASS_TEXT) { novoNome ->
                // Aqui você atualiza o nome do usuário no banco de dados
                usuarioAtual.nome = novoNome
                atualizarUsuario(usuarioAtual)
            }
        }
    }


    private fun atualizarUsuario(utilizador: Utilizador) {
        CoroutineScope(Dispatchers.IO).launch {
            database.UtilizadorDao().atualizar(utilizador)
            // Não esqueça de tratar a UI no thread principal se necessário.
        }
    }
    private fun mostrarDialogoDeEdicao(titulo: String, valorAtual: String, tipoInput: Int, acaoAoConfirmar: (String) -> Unit) {
        val editText = EditText(this).apply {
            inputType = tipoInput
            setText(valorAtual)
        }

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(editText)
            .setPositiveButton("Confirmar") { _, _ ->
                val novoValor = editText.text.toString()
                acaoAoConfirmar(novoValor)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }*/

}