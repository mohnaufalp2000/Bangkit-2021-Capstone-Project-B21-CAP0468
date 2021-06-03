package com.capstone.fresco.ui.auth.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivitySignUpBinding
import com.capstone.fresco.ui.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var docRef: DocumentReference
    private lateinit var userMap: HashMap<String, Any>
    private var userId: String? = null

    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnSignUp.setOnClickListener {
            checkDataUser()
        }
        binding.txtLogin.setOnClickListener {
            startActivity(
                Intent(
                    this@SignUpActivity,
                    LoginActivity::class.java
                )
            )
        }
    }

    //Check and get data from input user
    private fun checkDataUser() {
        //Get data
        val getUsername = binding.edtUsername.text.toString().trim()
        val getEmail = binding.edtEmail.text.toString().trim()
        val getPassword = binding.edtPassword.text.toString().trim()
        val getConfirmPassword = binding.edtConfirmPass.text.toString().trim()
        val getPhoneNumber = binding.edtPhone.text.toString().trim()

        //Check if is empty email and password
        if (getUsername.isEmpty() || getEmail.isEmpty() || getPassword.isEmpty() || getConfirmPassword.isEmpty() || getPhoneNumber.isEmpty()) {
            binding.apply {
                edtUsername.error = "Username can't be empty"
                edtUsername.requestFocus()
                edtEmail.error = "Email can't be empty"
                edtEmail.requestFocus()
                edtPassword.error = "Password can't be empty"
                edtPassword.requestFocus()
                edtConfirmPass.error = "Confirm Password can't be empty"
                edtConfirmPass.requestFocus()
                edtPhone.error = "Phone Number can't be empty"
                edtPhone.requestFocus()
            }
        }

        //Check email is valid
        if (!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
            binding.apply {
                edtEmail.error = "Email not valid"
                edtEmail.requestFocus()
            }
        }

        //Check long char of password
        if (getPassword.length < 8) {
            binding.apply {
                edtPassword.error = "Password lenght minimal 8 char"
                edtPassword.requestFocus()
            }
        }

        //Check if password and confirm password not same
        if (getPassword != getConfirmPassword) {
            binding.apply {
                edtPassword.error = "Password and Confirm Password is different"
                edtPassword.requestFocus()
                edtConfirmPass.error = "Password and Confirm Password is different"
                edtConfirmPass.requestFocus()
            }
        }

        //Check phone number is valid
        if (!Patterns.PHONE.matcher(getPhoneNumber).matches()) {
            binding.apply {
                edtPhone.error = "Phone number not valid"
                edtPhone.requestFocus()
            }
        }

        createUserAccount(getEmail, getPassword)
    }

    //Sign Up
    private fun createUserAccount(getEmail: String, getPassword: String) {
        val getUsername = binding.edtUsername.text.toString().trim()
        val getPhoneNumber = binding.edtPhone.text.toString().trim()

        auth.createUserWithEmailAndPassword(getEmail, getPassword)
            .addOnCompleteListener { task -> //Check success status for sign up
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Sign Up Success",
                        Toast.LENGTH_SHORT
                    ).show()
                    userId = auth.currentUser?.uid
                    docRef = db.collection("user").document(userId.toString())
                    userMap = HashMap()
                    binding.apply {
                        userMap["username"] = getUsername
                        userMap["email"] = getEmail
                        userMap["phone"] = getPhoneNumber
                    }
                    docRef.set(userMap).addOnSuccessListener {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Map Success $userId",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Map Failed $userId",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Intent(
                        this@SignUpActivity,
                        LoginActivity::class.java
                    ).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Error, Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
    }
}