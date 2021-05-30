@file:Suppress("DEPRECATION", "DEPRECATION")

package com.capstone.fresco.ui.auth.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.ui.auth.AuthActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class LoginConFailed : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    var progressDialog: ProgressDialog? = null  //Progres login
    private var googleButton: SignInButton? = null
    private var gso: GoogleSignInOptions? = null //Config Google SignIn
    private var mGoogleApiClient: GoogleApiClient? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: AuthStateListener? = null
    private val RC_SIGN_ID = 9001 //Kode SignIn
    private val TAG = "SignInAcivity" //Log Debugging
    private var user: FirebaseUser? = null //Check User login
    private var firebaseAnalytics: FirebaseAnalytics? = null

    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        googleButton = findViewById(R.id.login_google)
        FirebaseConnect()
        googleButton!!.setOnClickListener {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
            SignIn()
        }
    }

    //Config to connect with firebase
    private fun FirebaseConnect() {
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
            user = firebaseAuth.currentUser
            if (user != null) {
                startActivity(Intent(this@LoginConFailed, AuthActivity::class.java))
                finish()
            }
        }

        // Config GoogleSignIn
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    //Config for sign in with google account
    private fun SignIn() {
        val GSignIN: Intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(GSignIN, RC_SIGN_ID)
        progressDialog!!.setMessage("Mohon Tunggu...")
        progressDialog!!.isIndeterminate = true
        progressDialog!!.show()
    }

    //If user login successful
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this@LoginConFailed, AuthActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            "Autentikasi Gagal",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    progressDialog!!.dismiss()
                })
    }

    //If connection failed
    override fun onConnectionFailed(@NonNull connectionResult: ConnectionResult) {
        Log.d(TAG, "OnConnectionFailed$connectionResult")
        progressDialog!!.dismiss()
        Toast.makeText(getApplicationContext(), "Koneksi Gagal", Toast.LENGTH_SHORT).show()
    }

    protected fun onStart() {
        super.onStart()
        //If user already login
        firebaseAuth!!.addAuthStateListener(authStateListener!!)
    }

    //When stopped, remove listener
    protected fun onStop() {
        super.onStop()
        if (authStateListener != null) {
            firebaseAuth!!.removeAuthStateListener(authStateListener!!)
        }
    }

    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Get access for SignIn If Firebase Auth completed
        if (requestCode == RC_SIGN_ID) {
            val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess()) {
                val account: GoogleSignInAccount = result.getSignInAccount()
                firebaseAuthWithGoogle(account)
            } else {
                progressDialog!!.dismiss()
            }
        } else {
            progressDialog!!.dismiss()
        }
    }
}