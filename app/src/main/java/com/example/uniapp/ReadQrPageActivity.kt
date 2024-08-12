package com.example.uniapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.uniapp.model.BarcodeDataDto
import com.example.uniapp.model.EventDto
import com.example.uniapp.network.QrCodeApiService
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class ReadQrPageActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageAnalysis: ImageAnalysis
    private var eventId by Delegates.notNull<Int>()

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.read_qr_page)

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide default title

        // Handle back button click
        val backButton = findViewById<AppCompatButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Finish the activity to go back
        }

        // Retrieve the event ID from the Intent (if any)
        eventId = intent.getIntExtra("EVENT_ID", -1)
        if (eventId < 0){
            Toast.makeText(this, "No Event selected, cannot scan QR Code", Toast.LENGTH_LONG).show()
            finish()
        }

        // Initialize the camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            startCamera()
        }
    }

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(findViewById<PreviewView>(R.id.preview_view).surfaceProvider)
                }

            val barcodeScanner = BarcodeScanning.getClient()

            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image =
                        InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    barcodeScanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                when (barcode.valueType) {
                                    Barcode.TYPE_TEXT -> {
                                        val qrCodeContent = barcode.displayValue
                                        if (qrCodeContent != null) {
                                            // Pause scanning by unbinding imageAnalysis
                                            cameraProvider.unbind(imageAnalysis)
                                            lifecycleScope.launch {
                                                try {
                                                    val decryptedQrCodeContent =
                                                        handleQrCodeScanned(qrCodeContent)
                                                    runOnUiThread {

                                                        // Create the AlertDialog
                                                        val qrCodeReadDialog =
                                                            AlertDialog.Builder(this@ReadQrPageActivity)
                                                                .setTitle("Presence accepted!")
                                                                .setView(layoutInflater.inflate(R.layout.popup_qr_code_ok, null))
                                                                .setMessage("User ${decryptedQrCodeContent.name} has entered the event!")
                                                                .setPositiveButton("Read Again") { dialog: DialogInterface, _: Int ->
                                                                    // Rebind the imageAnalysis to resume scanning
                                                                    bindAnalysisToCamera()
                                                                    dialog.dismiss()
                                                                }
                                                                .setNegativeButton("Quit Reader") { _: DialogInterface, _: Int ->
                                                                    // Close the imageProxy and finish the activity
                                                                    imageProxy.close()
                                                                    finish()
                                                                }
                                                                .create()
                                                        qrCodeReadDialog.show()
                                                    }
                                                } catch (e: Exception) {
                                                    runOnUiThread {
                                                        // Create the AlertDialog
                                                        val qrCodeReadDialog =
                                                            AlertDialog.Builder(this@ReadQrPageActivity)
                                                                .setTitle("Error registering presence")
                                                                .setMessage("Unable to insert presence, already registered")
                                                                .setView(layoutInflater.inflate(R.layout.popup_qr_code_no, null))
                                                                .setPositiveButton("Read Again") { dialog: DialogInterface, _: Int ->
                                                                    // Rebind the imageAnalysis to resume scanning
                                                                    bindAnalysisToCamera()
                                                                    dialog.dismiss()
                                                                }
                                                                .setNegativeButton("Quit Reader") { _: DialogInterface, _: Int ->
                                                                    // Close the imageProxy and finish the activity
                                                                    imageProxy.close()
                                                                    finish()
                                                                }
                                                                .create()
                                                        qrCodeReadDialog.show()
                                                    }
                                                }
                                            }
                                        } else {
                                            runOnUiThread {
                                                Toast.makeText(this, "Unable to read QR Code", Toast.LENGTH_SHORT).show()
                                            }
                                            imageProxy.close()
                                            finish()
                                            return@addOnSuccessListener
                                        }
                                    }
                                }
                            }
                        }
                        .addOnFailureListener {
                            // Handle any errors
                            imageProxy.close()
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner, cameraSelector, preview, imageAnalysis)
            } catch(exc: Exception) {
                // Handle exception
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindAnalysisToCamera() {
        try {
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, imageAnalysis)
        } catch (exc: Exception) {
            // Handle exception
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun handleQrCodeScanned(qrCodeContent: String): BarcodeDataDto {
        // Call the suspend function to decrypt the QR code
        val decryptedContent = QrCodeApiService.decryptQrCode(qrCodeContent)
        QrCodeApiService.insertPresenceQrCode(qrCodeContent, eventId)
        return decryptedContent
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
