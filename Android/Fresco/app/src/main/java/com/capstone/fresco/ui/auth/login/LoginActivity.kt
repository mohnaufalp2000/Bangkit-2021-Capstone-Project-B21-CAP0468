package com.capstone.fresco.ui.auth.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.MainActivity
import com.capstone.fresco.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var register: Button? = null
    private var login: Button? = null
    private var email: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var progressBar: ProgressBar? = null
    private var auth: FirebaseAuth? = null
    private var listener: AuthStateListener? = null
    private var getEmail: String? = null
    private var getPassword: String? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //email = findViewById(R.id.getEmail)
        //password = findViewById(R.id.getPassword)
        //register = findViewById(R.id.register)
        //register.setOnClickListener(this)
        //login = findViewById(R.id.login)
        //login.setOnClickListener(this)
        //progressBar = findViewById(R.id.progressBar)
        //progressBar.setVisibility(View.GONE)

        //Hide Password
        password?.transformationMethod = PasswordTransformationMethod.getInstance()

        auth = FirebaseAuth.getInstance()

        ///Check Posisi User
        listener = AuthStateListener { firebaseAuth ->
            //Check if user already login / still not logout
            val user = firebaseAuth.currentUser
            if (user != null) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    //Add Listener
    override fun onStart() {
        super.onStart()
        auth!!.addAuthStateListener(listener!!)
    }

    //Remove Listener
    override fun onStop() {
        super.onStop()
        if (listener != null) {
            auth!!.removeAuthStateListener(listener!!)
        }
    }

    //Autentication user with email and password
    private fun loginUserAccount() {
        auth!!.signInWithEmailAndPassword(getEmail!!, getPassword!!)
            .addOnCompleteListener { task -> //Check login success
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error, Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }

    override fun onClick(v: View) {
        when (v.id) {
            //R.id.register -> startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            /*R.id.login -> {
                //Get data from input user
                getEmail = email?.getText().toString()
                getPassword = password?.getText().toString()

                //Check is empty email or password
                if (TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getPassword)) {
                    Toast.makeText(this, "Email or Password can't be empty", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    loginUserAccount()
                    progressBar!!.visibility = View.VISIBLE
                }
            }*/
        }
    }
}