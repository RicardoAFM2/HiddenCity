package com.digitalge.hiddencity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.digitalge.hiddencity.databinding.ActivityMainBinding
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupIconClicks()


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Home())
                .commit()
            highlightIcon(binding.home)
        }
        // Verifique se é a primeira criação da atividade após o login
        if (savedInstanceState == null) {
            val fragmentTag = intent.getStringExtra("OPEN_FRAGMENT")
            val userId = intent.getIntExtra("USER_ID", -1)

            // Escolha o fragmento inicial baseado na intenção
            val initialFragment = when (fragmentTag) {
                "Contas" -> Contas()
                "Conta_publica" -> Conta_publica().apply {
                    arguments = Bundle().apply { putInt("USER_ID", userId) }
                }
                else -> Home()  // Garantir que Home é o padrão caso não haja tags específicas
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, initialFragment)
                .commit()
        }
    }

    private fun replaceFragment(fragment: Fragment, fragmentTitle: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        binding.tituloPag.text = fragmentTitle  // Atualizar o título da página
    }


    private fun setupIconClicks() {
        // Associe cada ImageView a uma ação de clique que carrega o fragmento correspondente
        binding.home.apply {
            setOnClickListener { replaceFragment(Home()) }
            performClick()  // Chama o clique para carregar inicialmente o Home
        }
        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.home -> replaceFragment(Home(), "Home")
                R.id.guia -> replaceFragment(Lista_de_Guia(), "Guia")
                R.id.mapa -> replaceFragment(Mapa(), "Mapa")
                R.id.favoritos -> replaceFragment(Lista_de_Favoritos(), "Favoritos")
                R.id.conta -> replaceFragment(Contas(), "Conta")
            }
            highlightIcon(view as ImageView)
        }

        binding.home.setOnClickListener(clickListener)
        binding.guia.setOnClickListener(clickListener)
        binding.mapa.setOnClickListener(clickListener)
        binding.favoritos.setOnClickListener(clickListener)
        binding.conta.setOnClickListener(clickListener)
        binding.definicao.setOnClickListener {
            startActivity(Intent(this, Definicoes::class.java))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun highlightIcon(activeIcon: ImageView) {
        // Reset all icons to normal state
        listOf(binding.home, binding.guia, binding.mapa, binding.favoritos, binding.conta).forEach {
            it.clearAnimation()
            it.scaleX = 1.0f
            it.scaleY = 1.0f
            it.translationZ = 0f
            it.background = null
        }

        // Apply animation to the active icon
        val liftAnimator = AnimatorInflater.loadAnimator(this, R.animator.lift) as AnimatorSet
        liftAnimator.setTarget(activeIcon)
        liftAnimator.start()

        // Set a shadow effect
        val shadowDrawable = ResourcesCompat.getDrawable(resources, R.drawable.shadow_effect, null)
        activeIcon.background = shadowDrawable
    }
}

