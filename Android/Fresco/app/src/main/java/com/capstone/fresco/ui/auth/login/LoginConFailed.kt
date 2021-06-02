@file:Suppress("DEPRECATION", "DEPRECATION")

package com.capstone.fresco.ui.auth.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivityLoginBinding
import com.capstone.fresco.ui.auth.AuthActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

open class LoginConFailed : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private lateinit var gso: GoogleSignInOptions //Config Google SignIn
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: AuthStateListener
    private val RC_SIGN_ID = 9001 //Kode SignIn
    private val TAG = "SignInAcivity" //Log Debugging
    private lateinit var user: FirebaseUser //Check User login
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var progressDialog: ProgressDialog

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        firebaseConnect()
        binding.signInButton.setOnClickListener {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
            signIn()
        }
    }

    //Config to connect with firebase
    private fun firebaseConnect() {
        firebaseAnalytics =
            FirebaseAnalytics.getInstance(this) //Connect with Firebase Analytics
        firebaseAuth = FirebaseAuth.getInstance() //Connect with Firebase Auth
        progressDialog =
            ProgressDialog(this)

        //Auth Listener
        authStateListener = AuthStateListener { firebaseAuth -> /*
                     If User already login dan still not logout before, then
                     when open apps again, will straight to Main
                     */
            user = firebaseAuth.currentUser!!
            startActivity(Intent(this@LoginConFailed, AuthActivity::class.java))
            finish()
        }

        // Config GoogleSignIn
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            //.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    //Config for sign in with google account
    private fun signIn() {
        val gSignIn: Intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(gSignIn, RC_SIGN_ID)
        progressDialog.setMessage("Please wait...")
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    //If user login successful
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account?.id)
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this@LoginConFailed, AuthActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    progressDialog.dismiss()
                })
    }

    //If connection failed
    override fun onConnectionFailed(@NonNull connectionResult: ConnectionResult) {
        Log.d(TAG, "OnConnectionFailed$connectionResult")
        progressDialog.dismiss()
        Toast.makeText(applicationContext, "Connection failed", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        //If user already login
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    //When stopped, remove listener
    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Get access for SignIn If Firebase Auth completed
        if (requestCode == RC_SIGN_ID) {
            val result: GoogleSignInResult? = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if (result.isSuccess) {
                    val account: GoogleSignInAccount? = result.signInAccount
                    firebaseAuthWithGoogle(account)
                } else {
                    progressDialog.dismiss()
                }
            }
        } else {
            progressDialog.dismiss()
        }
    }
}