package com.digitalge.hiddencity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.digitalge.hiddencity.databinding.ActivityContasBinding
import com.digitalge.hiddencity.databinding.ActivityFormolarioBinding

class Formolario : AppCompatActivity() {

    private lateinit var binding: ActivityFormolarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormolarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Clicarnaimagem()
    }

    private fun Clicarnaimagem(){
        //Voltar para tras
        binding.voltra1.setOnClickListener {
            onBackPressed()
        }
    }
}