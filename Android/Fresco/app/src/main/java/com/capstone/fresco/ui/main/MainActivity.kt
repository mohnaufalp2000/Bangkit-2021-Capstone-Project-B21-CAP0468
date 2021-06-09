package com.capstone.fresco.ui.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.R
import com.capstone.fresco.databinding.ActivityMainBinding
import com.capstone.fresco.ui.HistoryActivity
import com.capstone.fresco.ui.ProfileActivity
import com.capstone.fresco.ui.auth.login.LoginActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import io.trialy.library.Constants
import io.trialy.library.Trialy
import io.trialy.library.TrialyCallback
import java.util.*
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {

    private lateinit var authListener: AuthStateListener
    private lateinit var auth: FirebaseAuth
    private lateinit var trialy: Trialy
    private var countDownTimer: CountDownTimer? = null
    private var ads: InterstitialAd? = null
    private var adIsLoading: Boolean = false
    private var ADS_DURATION = 5000L
    private var adIsInProgress = false
    private var timer = 0L
    private var doubleTapExit = false

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        trialy = Trialy(this, TRIALY_APP_KEY)


        binding.btnFruit.setOnClickListener {
            startActivity(Intent(this, CameraFruitActivity::class.java))
        }

        binding.btnPlant.setOnClickListener {
            startActivity(Intent(this, CameraPlantActivity::class.java))
        }

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

        ///Check Posisi User
        authListener = AuthStateListener { firebaseAuth ->
            //Check if user already login / still not logout
            val user = firebaseAuth.currentUser
            if (user != null) {
                trialy.startTrial(TRIALY_SKU, trialCallback)
                trialy.checkTrial(TRIALY_SKU, trialCallback)
            }
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_profile -> {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            ProfileActivity::class.java
                        )
                    )
                    true
                }
                R.id.action_history -> {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            HistoryActivity::class.java
                        )
                    )
                    true
                }
                else -> false
            }
        }
        MobileAds.initialize(this) {}

    }

    override fun onBackPressed() {
        if (doubleTapExit) {
            super.onBackPressed()
            return
        }

        doubleTapExit = true
        Toast.makeText(this, "Please tap back button again to exit", Toast.LENGTH_LONG).show()

        Handler(mainLooper).postDelayed(Runnable { doubleTapExit = false }, 2000)
    }


        private fun showAds() {
            if (ads != null) {
                ads?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                        ads = null
                        loadAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        Log.d(TAG, "Ad failed to show.")
                        ads = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        ads = null
                    }
                }
                ads?.show(this)
            } else {
                Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
                startAds()
            }
        }

        private fun startAds() {
            if (!adIsLoading && ads == null) {
                adIsLoading = true
                loadAd()
            }
            resumeAds(ADS_DURATION)
        }

        private fun resumeAds(millisec: Long) {
            adIsInProgress = true
            timer = millisec
            createTimer(millisec)
            countDownTimer?.start()
        }

        private fun createTimer(millisec: Long) {
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(millisec, 50) {
                override fun onTick(millisUntilFinished: Long) {
                    timer = millisUntilFinished
                }

                override fun onFinish() {
                    adIsInProgress = false
                    startActivity(Intent(this@MainActivity, CameraFruitActivity::class.java))
                }
            }
        }

        private fun loadAd() {
            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(
                this, AD_UNIT_ID, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError.message)
                        ads = null
                        adIsLoading = false
                        val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                                "message: ${adError.message}"
                        Toast.makeText(
                            this@MainActivity,
                            "onAdFailedToLoad() with error $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d(TAG, "Ad was loaded.")
                        ads = interstitialAd
                        adIsLoading = false
                        Toast.makeText(this@MainActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
                    }
                }
            )
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

        override fun onPause() {
            countDownTimer?.cancel()
            super.onPause()
        }

        override fun onResume() {
            super.onResume()
            if (adIsInProgress) {
                resumeAds(timer)
            }
        }

        private val trialCallback = TrialyCallback { status, timeRemaining, _ ->
            Log.i(TAG, "status: $status; time remaining: $timeRemaining")
            when (status) {
                Constants.STATUS_TRIAL_JUST_STARTED -> {
                    activatePremiumFeatures()
                    updateTimeRemainingLabel(timeRemaining)
                    val daysRemaining = (timeRemaining / (60 * 60 * 24)).toFloat().roundToLong()
                    showDialog(
                        "Trial started",
                        String.format(
                            Locale.ENGLISH,
                            "You can now try the premium features for %d days",
                            daysRemaining
                        ),
                        "OK"
                    )
                }
                Constants.STATUS_TRIAL_RUNNING -> {
                    activatePremiumFeatures()
                    updateTimeRemainingLabel(timeRemaining)

                }
                Constants.STATUS_TRIAL_JUST_ENDED -> {
                    deactivatePremiumFeatures()
                    updateTimeRemainingLabel(-1)
                }
                Constants.STATUS_TRIAL_OVER -> {
                }

                else -> Log.e(TAG, "Trialy response: " + Trialy.getStatusMessage(status))
            }
            Snackbar.make(
                findViewById(android.R.id.content),
                "" + Trialy.getStatusMessage(status),
                Snackbar.LENGTH_LONG
            )
                .setAction("OK", null).show()
            return@TrialyCallback
        }

        private fun activatePremiumFeatures() {
            ads = null
        }

        private fun deactivatePremiumFeatures() {
            showAds()
        }

        private fun updateTimeRemainingLabel(timeRemaining: Long) {
            if (timeRemaining == -1L) {
                binding.timeRemaining.visibility = View.GONE
                return
            }
            val daysRemaining = timeRemaining.toInt() / (60 * 60 * 24)
            binding.tvTimeRemaining.text =
                String.format(Locale.ENGLISH, "Your trial ends in %d days", daysRemaining)
            binding.timeRemaining.visibility = View.VISIBLE
        }

        private fun showDialog(title: String, message: String, buttonLabel: String) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(buttonLabel, null)
            val dialog = builder.create()
            dialog.show()
        }

        companion object {
            private const val TAG = "MainActivity"
            const val TRIALY_APP_KEY =
                "7KUNT46VJD4JHFDDYN0"
            const val TRIALY_SKU =
                "default"
            const val AD_UNIT_ID =
                "ca-app-pub-7074988547859559/7690066729" //TEST MODE ca-app-pub-3940256099942544/8691691433, must be replace with ca-app-pub-7074988547859559/7690066729
        }
    }
