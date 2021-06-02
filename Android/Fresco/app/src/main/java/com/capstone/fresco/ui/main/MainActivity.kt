package com.capstone.fresco.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivityMainBinding
//import com.capstone.fresco.ui.ads.AdsActivity
import com.capstone.fresco.ui.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class MainActivity : AppCompatActivity() {

    private lateinit var authListener: AuthStateListener
    private lateinit var auth: FirebaseAuth

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //Check if user have been logout
        authListener =
            AuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user == null) {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            LoginActivity::class.java
                        )
                    )
                    finish()
                }
            }
        binding.btnLogout.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "Logout Success",
                Toast.LENGTH_SHORT
            ).show()
            auth.signOut()
        }

        binding.btnAds.setOnClickListener {
//            startActivity(Intent(this@MainActivity, AdsActivity::class.java))
        }
    }

    //Add Listener
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    //Remove Listener
    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }
}