package com.krissphi.id.mykisah.ui.page.auth.register

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
import com.krissphi.id.mykisah.databinding.ActivityAuthRegisterBinding
import com.krissphi.id.mykisah.ui.page.auth.login.AuthLoginActivity

class AuthRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAction()
        setupObserver()
    }

    private fun setupAction() {
        binding.btnRegist.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerViewModel.register(name, email, password)
            } else {
                Toast.makeText(this, getString(R.string.form_empty), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObserver() {
        registerViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        registerViewModel.registrationResponse.observe(this) { result ->
            result.error?.let {
                if (!it) {
                    Toast.makeText(this, getString(R.string.regist_success), Toast.LENGTH_SHORT).show()
                     val intent = Intent(this, AuthLoginActivity::class.java)
                     startActivity(intent)
                     finish()
                }
            }
        }

        registerViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_LONG).show()
        }
    }


}