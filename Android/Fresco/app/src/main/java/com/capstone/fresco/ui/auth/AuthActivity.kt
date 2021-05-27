package com.capstone.fresco.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.fresco.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAuthBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}