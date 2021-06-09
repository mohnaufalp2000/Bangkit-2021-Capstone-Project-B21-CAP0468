@file:Suppress("DEPRECATION")

package com.capstone.fresco.ui.main

import android.R
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.capstone.fresco.R
import com.capstone.fresco.core.model.FruitResponse
import com.capstone.fresco.core.network.ConfigNetwork
import com.capstone.fresco.databinding.ActivityCameraFruitBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import io.trialy.library.Constants
import io.trialy.library.Trialy
import io.trialy.library.TrialyCallback
import com.capstone.fresco.ml.Freshrotten
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CameraFruitActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCameraFruitBinding.inflate(layoutInflater) }
    private lateinit var bitmap: Bitmap
    private var interpreter: Interpreter? = null
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private lateinit var trialy: Trialy
    private var countDownTimer: CountDownTimer? = null
    private var ads: InterstitialAd? = null
    private var adIsLoading: Boolean = false
    private var ADS_DURATION = 5000L
    private var adIsInProgress = false
    private var timer = 0L

    private lateinit var dialog : Dialog
  
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        trialy = Trialy(this, MainActivity.TRIALY_APP_KEY)

        // Scan again after scan a fruit
        binding.btnScanAgain.setOnClickListener {
            binding.apply {
                containerDetail.visibility = View.GONE
                containerScan.visibility = View.VISIBLE
                dataFruitContent.visibility = View.GONE
            }
        }

        // Scan a fruit
        binding.btnScan.setOnClickListener {
            binding.apply {
                containerDetail.visibility = View.VISIBLE
                containerScan.visibility = View.GONE
            }

            //showAds() //UNCOMMENT THIS IS FOR TEST, DELETE IF SCAN FEATURE IS DONE

            val conditions = CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .requireCharging()
                .requireDeviceIdle()
                .build()
            FirebaseModelDownloader.getInstance()
                .getModel(
                    "Fruit", DownloadType.LATEST_MODEL,
                    conditions
                )
                .addOnSuccessListener { model: CustomModel? ->
                    val modelFile = model?.file
                    if (modelFile != null) {
                        interpreter = Interpreter(modelFile)
                        scanFruit()
                    }

            if (this::bitmap.isInitialized){
                binding.apply {
                    containerDetail.visibility = View.VISIBLE
                    containerScan.visibility = View.GONE
                }
                scanFruit()
            } else {
                Toast.makeText(this, "Please input the image first", Toast.LENGTH_LONG).show()
            }
        }


        // Take picture with camera
        binding.btnCapture.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_TAKE_PICTURE)
        }

        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 100, 100, 3), DataType.FLOAT32)
        val resizedImage = resizeImage(bitmap, 200, 200, true)
        // Upload image from gallery
        binding.btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/"
            startActivityForResult(intent, REQUEST_UPLOAD_PICTURE)
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

    private fun scanFruit() {

        // Upload image from gallery
        binding.btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/"
            startActivityForResult(intent, REQUEST_UPLOAD_PICTURE)
        }

        toolbarSetup()
        dialog = Dialog(this)
    }

    private fun scanFruit() {
        val model = Freshrotten.newInstance(this)
        val input = TensorBuffer.createFixedSize(
            intArrayOf(1, 100, 100, 3),
            DataType.FLOAT32
        )
        val resizedImage = resizeImage(bitmap, 100, 100, false)

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(resizedImage)

        val modelOutput = tensorImage.buffer

        input.loadBuffer(modelOutput)

        interpreter?.run(input.buffer, modelOutput)

        val probabilities = input.floatArray

        val data = getTitleFruit(probabilities)

        val labels = "fruit-labels.txt"
        val reader = application.assets.open(labels).bufferedReader().use { it.readText() }
        val list = reader.split("\n")

        binding.txtTitle.text = list[data]
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.tbFruit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val modelOutput = tensorImage.buffer
        input.loadBuffer(modelOutput)

        val outputs = model.process(input)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        val data = getTitleFruit(outputFeature0)

        val labels = "freshrotten-labels.txt"
        val reader = application.assets.open(labels).bufferedReader().use { it.readText() }
        val list = reader.split("\n")

        binding.txtTitle.text = list[data]
        getDetailFruit(list[data])
    }

    private fun getDetailFruit(name: String) {
        when{
            name.subSequence(0, 5) == "Fresh" -> {
                    if (name.subSequence(6, 12) == "Apples"){
                        val getName = name.subSequence(6,11)
                        ConfigNetwork.getData().getAll(getName.toString()).enqueue(object : Callback<FruitResponse>{
                            override fun onResponse(
                                call: Call<FruitResponse>,
                                response: Response<FruitResponse>
                            ) {
                                binding.dataFruitContent.visibility = View.VISIBLE
                                val body = response.body()
                                val nutrition = response.body()?.nutritions
                                binding.apply {
                                    dataFruitContent.visibility = View.VISIBLE
                                    txtGenusFruit.text = body?.genus
                                    txtFamilyFruit.text = body?.family
                                    txtOrderFruit.text = body?.order
                                    txtCarbo.text = nutrition?.carbohydrates.toString()
                                    txtProtein.text = nutrition?.protein.toString()
                                    txtFat.text = nutrition?.fat.toString()
                                    txtCalories.text = nutrition?.calories.toString()
                                    txtSugar.text = nutrition?.calories.toString()
                                }
                            }
                            override fun onFailure(call: Call<FruitResponse>, t: Throwable) {
                            }
                        })
                    } else {
                        val getName = name.subSequence(6,12)
                        ConfigNetwork.getData().getAll(getName.toString()).enqueue(object : Callback<FruitResponse>{
                            override fun onResponse(
                                call: Call<FruitResponse>,
                                response: Response<FruitResponse>
                            ) {
                                binding.dataFruitContent.visibility = View.VISIBLE
                                val body = response.body()
                                val nutrition = response.body()?.nutritions
                                binding.apply {
                                    dataFruitContent.visibility = View.VISIBLE
                                    txtGenusFruit.text = body?.genus
                                    txtFamilyFruit.text = body?.family
                                    txtOrderFruit.text = body?.order
                                    txtCarbo.text = nutrition?.carbohydrates.toString()
                                    txtProtein.text = nutrition?.protein.toString()
                                    txtFat.text = nutrition?.fat.toString()
                                    txtCalories.text = nutrition?.calories.toString()
                                    txtSugar.text = nutrition?.calories.toString()
                                }
                            }
                            override fun onFailure(call: Call<FruitResponse>, t: Throwable) {
                            }
                        })
                    }
                }
            name.subSequence(0, 6) == "Rotten" -> {
                binding.apply {
                    btnScanAgain.visibility = View.GONE
                    dataFruitContent.visibility = View.GONE
                    dataRottenCauses.visibility = View.VISIBLE
                    dialog.setContentView(R.layout.rotten_fruit_dialog)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val btnRotten = dialog.findViewById<AppCompatButton>(R.id.btn_see_rotten)
                    btnRotten.setOnClickListener {
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }
        }
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.tbFruit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.tbFruit.setNavigationOnClickListener {
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

    private fun getTitleFruit(arr: FloatArray): Int {
        var index = 0
        var min = 0.0f
        val range = 0..130

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
                        this@CameraFruitActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    ads = interstitialAd
                    adIsLoading = false
                    Toast.makeText(this@CameraFruitActivity, "onAdLoaded()", Toast.LENGTH_SHORT)
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

        val range = 0..5

        for (i in range) {
            if (arr[i] > min) {
                index = i
                min = arr[i]
            }
        }
        return index
    }
    
    companion object {
        private const val TAG = "CameraFruitActivity"
        const val REQUEST_TAKE_PICTURE = 1
        const val REQUEST_UPLOAD_PICTURE = 2
    }
}