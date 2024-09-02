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
import com.example.uniapp.network.QrCodeApiService
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class ReadQrPageActivity : AppCompatActivity() {

    // Executor service to handle camera operations on a separate thread
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageAnalysis: ImageAnalysis
    private var eventId by Delegates.notNull<Int>() // Event ID to track the specific event

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001 // Request code for camera permission
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.read_qr_page) // Set the layout for this activity

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide the default title

        // Handle the back button click to finish the activity
        val backButton = findViewById<AppCompatButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Retrieve the event ID from the Intent (if passed) to identify the event
        eventId = intent.getIntExtra("EVENT_ID", -1)
        if (eventId < 0) {
            // If no event is selected, display a message and finish the activity
            Toast.makeText(this, "No Event selected, cannot scan QR Code", Toast.LENGTH_LONG).show()
            finish()
        }

        // Initialize the camera executor for managing camera operations on a separate thread
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Check for camera permission; if not granted, request it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            // If permission is granted, start the camera
            startCamera()
        }
    }

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalGetImage::class) // Opt into experimental API for image processing
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            // Setup camera preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(findViewById<PreviewView>(R.id.preview_view).surfaceProvider)
                }

            // Setup barcode scanner using ML Kit
            val barcodeScanner = BarcodeScanning.getClient()

            // Setup image analysis to analyze frames from the camera
            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // Set an analyzer to process the camera frames
            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    barcodeScanner.process(image) // Process the image for barcodes
                        .addOnSuccessListener { barcodes ->
                            // Iterate over all detected barcodes
                            for (barcode in barcodes) {
                                when (barcode.valueType) {
                                    Barcode.TYPE_TEXT -> {
                                        val qrCodeContent = barcode.displayValue
                                        if (qrCodeContent != null) {
                                            // Pause scanning by unbinding imageAnalysis
                                            cameraProvider.unbind(imageAnalysis)
                                            lifecycleScope.launch {
                                                try {
                                                    // Handle the scanned QR code
                                                    val decryptedQrCodeContent = handleQrCodeScanned(qrCodeContent)
                                                    runOnUiThread {
                                                        // Show a dialog indicating the presence was accepted
                                                        val qrCodeReadDialog =
                                                            AlertDialog.Builder(this@ReadQrPageActivity)
                                                                .setTitle("Presence accepted!")
                                                                .setView(layoutInflater.inflate(R.layout.popup_qr_code_ok, null))
                                                                .setMessage("User ${decryptedQrCodeContent.name} has entered the event!")
                                                                .setPositiveButton("Read Again") { dialog: DialogInterface, _: Int ->
                                                                    // Resume scanning by rebinding imageAnalysis
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
                                                        // Show a dialog indicating an error during the presence registration
                                                        val qrCodeReadDialog =
                                                            AlertDialog.Builder(this@ReadQrPageActivity)
                                                                .setTitle("Error registering presence")
                                                                .setMessage("Unable to insert presence, already registered")
                                                                .setView(layoutInflater.inflate(R.layout.popup_qr_code_no, null))
                                                                .setPositiveButton("Read Again") { dialog: DialogInterface, _: Int ->
                                                                    // Resume scanning by rebinding imageAnalysis
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
                                            // If QR code content is null, show a toast and finish the activity
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
                            // Handle any errors in barcode scanning
                            imageProxy.close()
                        }
                        .addOnCompleteListener {
                            // Always close the imageProxy to free resources
                            imageProxy.close()
                        }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Bind camera preview and image analysis to the camera lifecycle
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner, cameraSelector, preview, imageAnalysis)
            } catch (exc: Exception) {
                // Handle any errors during camera setup
            }

        }, ContextCompat.getMainExecutor(this))
    }

    // Helper function to rebind image analysis to the camera
    private fun bindAnalysisToCamera() {
        try {
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, imageAnalysis)
        } catch (exc: Exception) {
            // Handle any errors during rebinding
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun handleQrCodeScanned(qrCodeContent: String): BarcodeDataDto {
        // Decrypt the QR code content and register the presence for the event
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
            // If permission granted, start the camera
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                // If permission denied, show a toast and finish the activity
                Toast.makeText(this, "Camera permission is required to scan QR codes.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Shutdown the camera executor when the activity is destroyed
        cameraExecutor.shutdown()
    }
}
