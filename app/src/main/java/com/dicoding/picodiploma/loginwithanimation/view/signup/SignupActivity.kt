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
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupViewModel()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(this)
        signupViewModel = ViewModelProvider(this, factory)[SignupViewModel::class.java]

        // Observe loading state
        signupViewModel.isLoading.observe(this) { isLoading ->
            toggleProgressBar(isLoading)
        }
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
            val email = binding.emailEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Error! Semua harus diisi dengan benar!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Trigger register logic
            signupViewModel.register(name, email, password)

            signupViewModel.registerResponse.observe(this@SignupActivity) { response ->
                if (!response.error) {
                    showSuccessDialog()
                    signupViewModel.registerResponse.removeObservers(this@SignupActivity)
                } else {
                    Toast.makeText(
                        this,
                        "Error! Pendaftaran gagal: ${response.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun toggleProgressBar(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.signupButton.isEnabled = !isLoading
    }

    private fun showSuccessDialog() {
        alertDialog = AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Akun berhasil dibuat. Silahkan Login!")
            setPositiveButton("Login") { _, _ -> finish() }
        }.create()
        alertDialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
        alertDialog = null
    }
}

