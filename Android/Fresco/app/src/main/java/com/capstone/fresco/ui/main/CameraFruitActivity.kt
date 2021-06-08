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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.capstone.fresco.R
import com.capstone.fresco.core.model.FruitResponse
import com.capstone.fresco.core.network.ConfigNetwork
import com.capstone.fresco.databinding.ActivityCameraFruitBinding
import com.capstone.fresco.ml.Freshrotten
import com.capstone.fresco.ui.main.CameraPlantActivity.Companion.REQUEST_TAKE_PICTURE
import com.capstone.fresco.ui.main.CameraPlantActivity.Companion.REQUEST_UPLOAD_PICTURE
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class CameraFruitActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCameraFruitBinding.inflate(layoutInflater) }
    private lateinit var bitmap: Bitmap
    private var interpreter: Interpreter? = null
    private var localModel: Freshrotten? = null
    private var probabilities: FloatArray? = null

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
                dataFruitContent.visibility = View.GONE
            }
        }

        // Scan a fruit
        binding.btnScan.setOnClickListener {
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

        // Upload image from gallery
        binding.btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/"
            startActivityForResult(intent, REQUEST_UPLOAD_PICTURE)
        }

        toolbarSetup()
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
        const val REQUEST_TAKE_PICTURE = 1
        const val REQUEST_UPLOAD_PICTURE = 2
    }

}
