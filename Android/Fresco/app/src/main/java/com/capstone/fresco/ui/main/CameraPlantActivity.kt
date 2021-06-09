package com.capstone.fresco.ui.main

import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.capstone.fresco.databinding.ActivityCameraPlantBinding
import com.capstone.fresco.ml.Leaf
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import io.trialy.library.Constants
import io.trialy.library.Trialy
import io.trialy.library.TrialyCallback
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class CameraPlantActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCameraPlantBinding.inflate(layoutInflater) }
    private lateinit var bitmap: Bitmap
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private lateinit var trialy: Trialy
    private var countDownTimer: CountDownTimer? = null
    private var ads: InterstitialAd? = null
    private var adIsLoading: Boolean = false
    private var ADS_DURATION = 5000L
    private var adIsInProgress = false
    private var timer = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        trialy = Trialy(this, MainActivity.TRIALY_APP_KEY)

        // Scan again after scan a plant
        binding.btnScanAgain.setOnClickListener {
            binding.apply {
                containerDetail.visibility = View.GONE
                containerScan.visibility = View.VISIBLE
            }
        }

        // Scan a plant
        binding.btnScan.setOnClickListener {

            binding.apply {
                containerDetail.visibility = View.VISIBLE
                containerScan.visibility = View.GONE
            }

            //showAds() //UNCOMMENT THIS IS FOR TEST, DELETE IF SCAN FEATURE IS DONE

            val labels = "leaf-labels.txt"
            val input = application.assets.open(labels).bufferedReader().use { it.readText() }
            val list = input.split("\n")

            // From model
            val model = Leaf.newInstance(this)

            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 100, 100, 3), DataType.FLOAT32)
            val resizedImage = resizeImage(bitmap, 200, 200, true)

            val image = TensorImage.fromBitmap(resizedImage)
            inputFeature0.loadBuffer(image.buffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val data = getString(outputFeature0.floatArray)

            binding.txtTitle.text = list[data]

            model.close()

        }

        // Take picture with camera
        binding.btnCapture.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CameraFruitActivity.REQUEST_TAKE_PICTURE)
        }

        // Upload image from gallery
        binding.btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/"
            startActivityForResult(intent, CameraFruitActivity.REQUEST_UPLOAD_PICTURE)
        }

        ///Check Posisi User
        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            //Check if user already login / still not logout
            val user = firebaseAuth.currentUser
            if (user != null) {
                trialy.startTrial(MainActivity.TRIALY_SKU, trialCallback)
                trialy.checkTrial(MainActivity.TRIALY_SKU, trialCallback)
            }
        }

        toolbarSetup()
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.tbPlant)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.tbPlant.setNavigationOnClickListener {
            finish()
        }
    }

    private fun resizeImage(bitmap: Bitmap, width: Int, height: Int, filter: Boolean): Bitmap? =
        Bitmap.createScaledBitmap(bitmap, width, height, filter)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == REQUEST_TAKE_PICTURE && resultCode == RESULT_OK -> {
                bitmap = data?.extras?.get("data") as Bitmap
                binding.imgCapture.setImageBitmap(bitmap)
            }
            requestCode == REQUEST_UPLOAD_PICTURE && resultCode == RESULT_OK -> {
                binding.imgCapture.setImageURI(data?.data)
                val uri: Uri? = data?.data

                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            }
        }
    }

    private fun getString(arr: FloatArray): Int {

        var index = 0
        var min = 0.0f
        val range = 0..50


        for (i in range) {
            if (arr[i] > min) {
                index = i
                min = arr[i]
            }
        }
        return index
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
            }
        }
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this, MainActivity.AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    ads = null
                    adIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    Toast.makeText(
                        this@CameraPlantActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    ads = interstitialAd
                    adIsLoading = false
                    Toast.makeText(this@CameraPlantActivity, "onAdLoaded()", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
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
            }
            Constants.STATUS_TRIAL_RUNNING -> {
                activatePremiumFeatures()
                updateTimeRemainingLabel(timeRemaining)

            }
            Constants.STATUS_TRIAL_JUST_ENDED -> {
                binding.btnScan.setOnClickListener {
                    deactivatePremiumFeatures()
                }
                updateTimeRemainingLabel(-1)
            }
            Constants.STATUS_TRIAL_OVER -> {
            }

            else -> Log.e(TAG, "Trialy response: " + Trialy.getStatusMessage(status))
        }
        Snackbar.make(
            findViewById(R.id.content),
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
            return
        }
    }


    companion object {
        private const val TAG = "CameraPlantActivity"
        const val REQUEST_TAKE_PICTURE = 1
        const val REQUEST_UPLOAD_PICTURE = 2
    }
}