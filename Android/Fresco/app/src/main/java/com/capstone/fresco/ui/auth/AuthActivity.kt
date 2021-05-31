package com.capstone.fresco.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAuthBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //val logout: Button = findViewById(R.id.logout)
        /*logout.setOnClickListener { //Digunakan Untuk Logout
            FirebaseAuth.getInstance().signOut()
            finish()
        }*/
    }
}