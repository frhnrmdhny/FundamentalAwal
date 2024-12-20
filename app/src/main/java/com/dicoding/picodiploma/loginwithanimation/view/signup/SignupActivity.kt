package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[SignupViewModel::class.java]


        setupView()
        setupAction()
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.signupButton.isEnabled = !isLoading
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            if (email.isEmpty() || email.length < 8) {
                binding.passwordEditText.error = "Wrong format email!"
                return@setOnClickListener
            }

            val password = binding.passwordEditText.text.toString()

            if (password.isEmpty() || password.length < 8) {
                binding.passwordEditText.error = "Password must be at least 8 characters long."
                return@setOnClickListener
            }

            showLoading(true)

            // Panggil API register melalui AuthViewModel
            signupViewModel.register(name, email, password) { success, message ->
                showLoading(false)
                if (success) {
                    Toast.makeText(this, "Register Success!: $message", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Register Failed!: $message", Toast.LENGTH_SHORT).show()
                }


                if (success && !isFinishing && !isDestroyed) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Akun berhasil dibuat dan siap jadi nih. Yuk, login!.")
                        setPositiveButton("Lanjut bang") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

}

