package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
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
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                showAlertDialog(
                    title = "Pendaftaran Gagal",
                    message = "Semua kolom harus diisi."
                )
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showAlertDialog(
                    title = "Pendaftaran Gagal",
                    message = "Format email tidak valid."
                )
                return@setOnClickListener
            }

            if (password.length < 6) {
                showAlertDialog(
                    title = "Pendaftaran Gagal",
                    message = "Password harus terdiri dari minimal 6 karakter."
                )
                return@setOnClickListener
            }

            // Panggil API register
            viewModel.register(email, password) { response ->
                runOnUiThread {
                    if (response.success == true) {
                        Log.d("Register", "Registration successful: ${response.message}")
                        showAlertDialog(
                            title = "Selamat!",
                            message = "Akun dengan email $email berhasil dibuat.",
                            positiveButtonText = "Lanjut",
                            positiveButtonAction = { finish() }
                        )
                    } else {
                        Log.e("Register", "Registration failed: ${response.message}")
                        showAlertDialog(
                            title = "Pendaftaran Gagal",
                            message = response.message ?: "Terjadi kesalahan."
                        )
                    }
                }
            }
        }
    }

    // Fungsi untuk menampilkan dialog
    private fun showAlertDialog(
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        positiveButtonAction: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveButtonText) { _, _ ->
                positiveButtonAction?.invoke()
            }
            create()
            show()
        }
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logoImageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val subtitle = ObjectAnimator.ofFloat(binding.subtitleTextView, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.emailInputLayout, View.ALPHA, 1f).setDuration(500)
        val passwordInput = ObjectAnimator.ofFloat(binding.passwordInputLayout, View.ALPHA, 1f).setDuration(500)
        val registerButton = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)
        val loginLink = ObjectAnimator.ofFloat(binding.loginTextView, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                subtitle,
                emailInput,
                passwordInput,
                registerButton,
                loginLink
            )
            startDelay = 500
        }.start()
    }
}
