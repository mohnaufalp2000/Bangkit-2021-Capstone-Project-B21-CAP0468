package com.capstone.fresco.ui.auth

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.R
import com.capstone.fresco.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth

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