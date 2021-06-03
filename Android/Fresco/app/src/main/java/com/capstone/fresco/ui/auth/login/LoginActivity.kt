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

        ///Check Posisi User
        listener = AuthStateListener { firebaseAuth ->
            //Check if user already login / still not logout
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

    //Add Listener
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(listener)
    }

    //Remove Listener
    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(listener)
    }

    //Autentication user with email and password
    private fun loginUserAccount() {
        //Get data from input user
        val getEmail = binding.edtEmail.text.toString().trim()
        val getPassword = binding.edtPassword.text.toString().trim()

        auth.signInWithEmailAndPassword(getEmail, getPassword)
            .addOnCompleteListener { task -> //Check login success
                if (task.isSuccessful) {
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

        //Check is empty email or password
        if (getEmail.isEmpty() || getPassword.isEmpty()) {
            binding.apply {
                edtEmail.error = "Email can't be empty"
                edtEmail.requestFocus()
                edtPassword.error = "Password can't be empty"
                edtPassword.requestFocus()
            }
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }

        //Check email is valid
        if (!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
            binding.apply {
                edtEmail.error = "Email not valid"
                edtEmail.requestFocus()
            }
        }
    }
}