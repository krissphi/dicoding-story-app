package com.krissphi.id.mykisah.ui.page.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.krissphi.id.mykisah.R
import com.krissphi.id.mykisah.data.local.UserPreference
import com.krissphi.id.mykisah.data.local.dataStore
import com.krissphi.id.mykisah.databinding.ActivityWelcomeBinding
import com.krissphi.id.mykisah.ui.page.auth.login.AuthLoginActivity
import com.krissphi.id.mykisah.ui.page.auth.register.AuthRegisterActivity
import com.krissphi.id.mykisah.ui.page.story.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, AuthLoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegist.setOnClickListener {
            val intent = Intent(this, AuthRegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgIcon, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.btnRegist, View.ALPHA, 1f).setDuration(100)
        val title = ObjectAnimator.ofFloat(binding.tvHeading, View.ALPHA, 1f).setDuration(100)
        val desc = ObjectAnimator.ofFloat(binding.tvMessage, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }
}