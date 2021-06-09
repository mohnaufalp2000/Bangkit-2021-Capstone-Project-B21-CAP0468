package com.capstone.fresco.ui.auth.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivityLoginBinding
import com.capstone.fresco.ui.auth.password.ResetPassActivity
import com.capstone.fresco.ui.auth.signup.SignUpActivity
import com.capstone.fresco.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var listener: AuthStateListener

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        auth = FirebaseAuth.getInstance()

        listener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {
            loginUserAccount()
        }

        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        binding.txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ResetPassActivity::class.java))
        }

    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(listener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(listener)
    }

    private fun loginUserAccount() {
        val getEmail = binding.edtEmail.text.toString().trim()
        val getPassword = binding.edtPassword.text.toString().trim()

        auth.signInWithEmailAndPassword(getEmail, getPassword)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.VISIBLE
                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "Login Success",
                        Toast.LENGTH_SHORT
                    ).show()
                    Intent(this@LoginActivity, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error, Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.GONE
                }
            }

        if (getEmail.isEmpty() || getPassword.isEmpty()) {
            binding.apply {
                edtEmail.error = "Email can't be empty"
                edtEmail.requestFocus()
                edtPassword.error = "Password can't be empty"
                edtPassword.requestFocus()
            }
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
            binding.apply {
                edtEmail.error = "Email not valid"
                edtEmail.requestFocus()
            }
        }
    }
}