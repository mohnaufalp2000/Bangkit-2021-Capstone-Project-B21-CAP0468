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
            return@setOnClickListener
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

    private fun checkDataUser() {
        val getUsername = binding.edtUsername.text.toString().trim()
        val getEmail = binding.edtEmail.text.toString().trim()
        val getPassword = binding.edtPassword.text.toString().trim()
        val getConfirmPassword = binding.edtConfirmPass.text.toString().trim()
        val getPhoneNumber = binding.edtPhone.text.toString().trim()

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

        if (!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
            binding.apply {
                edtEmail.error = "Email not valid"
                edtEmail.requestFocus()
            }
        }

        if (getPassword.length < 8) {
            binding.apply {
                edtPassword.error = "Password lenght minimal 8 char"
                edtPassword.requestFocus()
            }
        }

        if (getPassword != getConfirmPassword) {
            binding.apply {
                edtPassword.error = "Password and Confirm Password is different"
                edtPassword.requestFocus()
                edtConfirmPass.error = "Password and Confirm Password is different"
                edtConfirmPass.requestFocus()
            }
        }

        if (!Patterns.PHONE.matcher(getPhoneNumber).matches()) {
            binding.apply {
                edtPhone.error = "Phone number not valid"
                edtPhone.requestFocus()
            }
        }

        createUserAccount(getEmail, getPassword)
    }

    private fun createUserAccount(getEmail: String, getPassword: String) {
        val getUsername = binding.edtUsername.text.toString().trim()
        val getPhoneNumber = binding.edtPhone.text.toString().trim()

        auth.createUserWithEmailAndPassword(getEmail, getPassword)
            .addOnCompleteListener { task ->
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
                    Intent(
                        this@SignUpActivity,
                        LoginActivity::class.java
                    ).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    emailAlreadyUse(getEmail)
                    Toast.makeText(
                        this@SignUpActivity,
                        "Error can't Sign Up, Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
    }

    private fun emailAlreadyUse(getEmail: String) {
        auth.fetchSignInMethodsForEmail(getEmail)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Email has been used",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Email is available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}