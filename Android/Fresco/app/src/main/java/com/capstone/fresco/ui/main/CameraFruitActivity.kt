package com.capstone.fresco.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivityAuthBinding
import com.capstone.fresco.databinding.ActivityCameraFruitBinding
import com.capstone.fresco.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class CameraFruitActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCameraFruitBinding.inflate(layoutInflater) }
    private lateinit var bitmap : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
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

            // From model
            val model = Model.newInstance(this)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 100, 100, 3), DataType.FLOAT32)

            val buffer = TensorImage.fromBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, true))
            val byteBuffer = buffer.buffer
            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            binding.txtTitle.text = outputFeature0.toString()

            model.close()

        }

        // Take picture with camera
        binding.btnCapture.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 1)
        }

        // UPload image from gallery
        binding.btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/"
            startActivityForResult(intent, 2)
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
            }
        }
    }

    companion object{
        const val REQUEST_TAKE_PICTURE = 1
        const val REQUEST_UPLOAD_PICTURE = 2
    }

}