package com.capstone.fresco

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.ui.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class MainActivity : AppCompatActivity() {

    private var authListener: AuthStateListener? = null
    private var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val logout: Button = findViewById(R.id.logout)

        auth = FirebaseAuth.getInstance()

        //Check if user have been logout
        authListener =
            AuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user == null) {
                    Toast.makeText(this@MainActivity, "Logout Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }
        /*logout.setOnClickListener {
            auth!!.signOut()
        }*/
    }

    //Add Listener
    override fun onStart() {
        super.onStart()
        auth!!.addAuthStateListener(authListener!!)
    }

    //Remove Listener
    override fun onStop() {
        super.onStop()
        if (authListener != null) {
            auth!!.removeAuthStateListener(authListener!!)
        }
    }
}