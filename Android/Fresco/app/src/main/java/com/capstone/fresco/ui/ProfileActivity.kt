package com.capstone.fresco.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivityProfileBinding
import com.capstone.fresco.ui.auth.login.LoginActivity
import com.capstone.fresco.ui.auth.password.ChangePassActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private var userId: String? = null
    private lateinit var docRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userId = auth.currentUser?.uid

        docRef = db.collection("user").document(userId.toString())
        docRef.get().addOnSuccessListener {
            if (it != null) {
                Log.d(TAG, "Success on $userId")
                binding.apply {
                    textUsername.text = it.getString("username")
                    textEmail.text = it.getString("email")
                    textPhone.text = it.getString("phone")
                }
            } else {
                Log.d(TAG, "No such doc")
            }
        }
            .addOnFailureListener {
                Log.d(TAG, "Failed")
            }
        binding.btnLogout.setOnClickListener {
            Toast.makeText(
                this@ProfileActivity,
                "Logout Success",
                Toast.LENGTH_SHORT
            ).show()
            auth.signOut()
            startActivity(
                Intent(
                    this@ProfileActivity,
                    LoginActivity::class.java
                )
            )
            return@setOnClickListener
        }

        binding.btnChangePass.setOnClickListener {
            startActivity(
                Intent(
                    this@ProfileActivity,
                    ChangePassActivity::class.java
                )
            )
        }
    }
}