package com.capstone.fresco.ui.auth.signup

import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private var email: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var regButton: Button? = null
    private var progressBar: ProgressBar? = null
    private var auth: FirebaseAuth? = null
    private var getEmail: String? = null
    private var getPassword: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        email = findViewById(R.id.regEmail)
        password = findViewById(R.id.regPassword)
        regButton = findViewById(R.id.regUser)
        progressBar = findViewById(R.id.progressBar)
        progressBar.setVisibility(View.GONE)
        auth = FirebaseAuth.getInstance()

        //Hide Password
        password.setTransformationMethod(PasswordTransformationMethod.getInstance())
        regButton.setOnClickListener(View.OnClickListener {
            checkDataUser()
        })
    }

    //Check and get data from input user
    private fun checkDataUser() {
        //Get data
        getEmail = email.getText().toString()
        getPassword = password.getText().toString()

        //Check if is empty email and password
        if (TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getPassword)) {
            Toast.makeText(this, "Email or Password can't be empty", Toast.LENGTH_SHORT).show()
        } else {
            //Check long char of password
            if (getPassword!!.length < 6) {
                Toast.makeText(this, "Password lenght minimal 6 char ", Toast.LENGTH_SHORT)
                    .show()
            } else {
                progressBar!!.visibility = View.VISIBLE
                createUserAccount()
            }
        }
    }

    //Sign Up
    private fun createUserAccount() {
        auth!!.createUserWithEmailAndPassword(getEmail!!, getPassword!!)
            .addOnCompleteListener { task -> //Check success status for sign up
                if (task.isSuccessful) {
                    Toast.makeText(this@SignUpActivity, "Sign Up Success", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Error, Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }
}