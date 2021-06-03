package com.capstone.fresco.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivityAuthBinding
import com.capstone.fresco.ui.auth.login.LoginActivity
import com.capstone.fresco.ui.main.MainActivity

class AuthActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAuthBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            startActivity(
                Intent(
                    this@AuthActivity,
                    LoginActivity::class.java
                )
            )
            return@setOnClickListener
        }

        binding.btnSkip.setOnClickListener {
            startActivity(
                Intent(
                    this@AuthActivity,
                    MainActivity::class.java
                )
            )
            return@setOnClickListener
        }
    }
}