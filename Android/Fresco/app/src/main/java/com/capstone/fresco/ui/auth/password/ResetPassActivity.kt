package com.capstone.fresco.ui.auth.password

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivityResetPassBinding
import com.capstone.fresco.ui.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class ResetPassActivity : AppCompatActivity() {

    private val binding by lazy { ActivityResetPassBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnResetPass.setOnClickListener {
            onResetPass()
            return@setOnClickListener
        }

    }

    private fun onResetPass() {
        val getEmail = binding.edtEmail.text.toString().trim()

        //Check is empty email or password
        if (getEmail.isEmpty()) {
            binding.apply {
                edtEmail.error = "Email can't be empty"
                edtEmail.requestFocus()
            }
        }

        //Check email is valid
        if (!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
            binding.apply {
                edtEmail.error = "Email not valid"
                edtEmail.requestFocus()
            }
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(getEmail)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Check your email to reset password",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    startActivity(
                        Intent(
                            this@ResetPassActivity,
                            LoginActivity::class.java
                        ).also { intent ->
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                } else {
                    Toast.makeText(
                        this,
                        "${it.exception?.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }
}