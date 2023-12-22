package com.rizkyizh.panopticon.ui.camera

import ImageClassifierHelper
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.rizkyizh.panopticon.MainActivity
import com.rizkyizh.panopticon.databinding.ActivityCameraBinding
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            MainActivity.REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)




        if (allPermissionsGranted()){
            startCamera()
        }else{
            Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
        }


        binding.btnExit.setOnClickListener {
            finish()
        }
    }


    private fun startCamera() {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            imageClassifierListener = object : ImageClassifierHelper.ClassifierListener{
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: Float, inferenceTime: Long) {
                    runOnUiThread {
                        results.let { it ->
                            Log.d("RESULT", it.toString())
                    //                            if (it.isNotEmpty()) {
                    //                                println(it)
                    //                                Log.d("RESULT", it.toString())
                    ////                                val sortedCategories =
                    ////                                    it[0].categories.sortedByDescending { it?.score }
                    ////                                val displayResult =
                    ////                                    sortedCategories.joinToString("\n") {
                    ////                                        "${it.label} " + NumberFormat.getPercentInstance().format(it.score).trim()
                    ////                                    }
                    ////                                binding.tvResult.text = displayResult
                    //                            }
                        }
                    }
                }

            }
        )



        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val imageAnalyzer =
                ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(binding.viewFinder.display.rotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
                    .also {
                        it.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                            imageClassifierHelper.classify(image)
                        }
                    }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }

    companion object {
        private const val TAG = "CameraActivity"
    }
}