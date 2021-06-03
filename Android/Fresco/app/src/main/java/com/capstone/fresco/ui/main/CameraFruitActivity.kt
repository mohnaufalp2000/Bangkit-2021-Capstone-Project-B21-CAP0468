package com.capstone.fresco.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.capstone.fresco.databinding.ActivityCameraFruitBinding
import com.capstone.fresco.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.lang.Byte.decode
import java.util.*

class CameraFruitActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCameraFruitBinding.inflate(layoutInflater) }
    private lateinit var bitmap : Bitmap

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

        // Scan a fruit
        binding.btnScan.setOnClickListener {
            binding.apply {
                containerDetail.visibility = View.VISIBLE
                containerScan.visibility = View.GONE
            }

            val labels = "fruit-labels.txt"
            val input = application.assets.open(labels).bufferedReader().use { it.readText() }
            val list = input.split("\n")

            // From model
            val model = Model.newInstance(this)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 100, 100, 3), DataType.FLOAT32)
            val resizedImage = Bitmap.createScaledBitmap(bitmap , 200, 200, true)

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
            startActivityForResult(intent, REQUEST_TAKE_PICTURE)
        }

        // Upload image from gallery
        binding.btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/"
            startActivityForResult(intent, REQUEST_UPLOAD_PICTURE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when{
            requestCode == REQUEST_TAKE_PICTURE && resultCode == RESULT_OK -> {
                bitmap = data?.extras?.get("data") as Bitmap
                binding.imgCapture.setImageBitmap(bitmap)
            }
            requestCode == REQUEST_UPLOAD_PICTURE && resultCode == RESULT_OK -> {
                binding.imgCapture.setImageURI(data?.data)
                val uri : Uri? =  data?.data

                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            }
        }
    }

    fun getString(arr: FloatArray) : Int {

        var index = 0
        var min = 0.0f
        val range = 0..130


        for(i in range){
            if (arr[i]>min){
                index = i
                min = arr[i]
            }
        }
        return index
    }

    companion object{
        const val REQUEST_TAKE_PICTURE = 1
        const val REQUEST_UPLOAD_PICTURE = 2
    }

}