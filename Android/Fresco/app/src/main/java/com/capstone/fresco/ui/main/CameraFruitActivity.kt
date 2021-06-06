@file:Suppress("DEPRECATION")

package com.capstone.fresco.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.capstone.fresco.databinding.ActivityCameraFruitBinding
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class CameraFruitActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCameraFruitBinding.inflate(layoutInflater) }
    private lateinit var bitmap: Bitmap
    private var interpreter: Interpreter? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Scan again after scan a fruit
        binding.btnScanAgain.setOnClickListener {
            binding.apply {
                containerDetail.visibility = View.GONE
                containerScan.visibility = View.VISIBLE
            }
        }

        binding.btnScan.setOnClickListener {

            binding.apply {
                containerDetail.visibility = View.VISIBLE
                containerScan.visibility = View.GONE
            }
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
                }
        }


            // Take picture with camera
            binding.btnCapture.setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_TAKE_PICTURE)
            }

            // Upload image from gallery
            binding.btnUpload.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/"
                startActivityForResult(intent, REQUEST_UPLOAD_PICTURE)
            }

            toolbarSetup()
        }

        private fun scanFruit() {
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

        companion object {
            const val REQUEST_TAKE_PICTURE = 1
            const val REQUEST_UPLOAD_PICTURE = 2
        }

    }