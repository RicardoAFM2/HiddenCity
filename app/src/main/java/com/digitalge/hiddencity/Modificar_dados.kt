package com.digitalge.hiddencity

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

        Clicarnaimagem()


    }

    private fun Clicarnaimagem(){
        binding.voltar.setOnClickListener {
            onBackPressed()
        }

        //binding.ModificasNome.textViewItem.setOnClickListener {
            //mostrarDialogoDeAtualizacao(1, "Nome")
        //}
    }

    //private fun mostrarDialogoDeAtualizacao(ID: Int, tipodeDado: String){
    //val builder = AlertDialog.Builder(this)
    //builder.setTitle("Modificar $tipodeDado")

    //val input = EditText(this)
    //input.inputType = InputType.TYPE_CLASS_TEXT
    //builder.setView(input)

    //builder.setPositiveButton("Confirmar"){_, _ ->
    //val novoValor = input.text.toString()
    //if (tipodeDado == "Nome")
    //atualizarNome(novoValor, ID)
    //}
    //builder.setNegativeButton("Cancelar"){dialog, _ ->
    //dialog.cancel()
    //}
    //builder.show()
    //}

    //private fun atualizarNome(novoNome: String, ID: Int){
    //CoroutineScope(Dispatchers.IO).launch {
    //val db = AppDatabase.getDatabase(this@Modificar_dados)
    //db.UtilizadorDao().atualizarNomeDoUtilizador(novoNome, ID)
    //}
    //}


}