package com.krissphi.id.mykisah.ui.page.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.krissphi.id.mykisah.R
import com.krissphi.id.mykisah.data.repository.ViewModelFactory
import com.krissphi.id.mykisah.databinding.ActivityAuthLoginBinding
import com.krissphi.id.mykisah.ui.page.story.main.MainActivity

class AuthLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAction()
        setupObserver()
    }

    private fun setupAction(){
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(this, getString(R.string.form_empty), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObserver(){
        loginViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        loginViewModel.loginResponse.observe(this) { result ->
            result.error?.let {
                if (!it) {
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }

            }
        }

        loginViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_LONG).show()
        }
    }
}