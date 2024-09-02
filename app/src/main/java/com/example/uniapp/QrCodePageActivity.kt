package com.example.uniapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.uniapp.network.QrCodeApiService
import com.example.uniapp.util.NavigationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QrCodePageActivity : AppCompatActivity() {

    // Declare variables for the progress bar and QR code image view
    private lateinit var progressBar: ProgressBar
    private lateinit var qrCodeImage: ImageView

    // This annotation indicates that the minimum API level required for this method is Android O (API level 26)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Redirect to the login screen if the user is not logged in
        NavigationUtils.returnToLoginIfNotLogged(this)

        // Call the superclass's onCreate method to perform standard initialization
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_page) // Set the layout for this activity

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide the default title

        // Initialize the progress bar and QR code image view from the layout
        progressBar = findViewById(R.id.timerProgressBar)
        qrCodeImage = findViewById(R.id.qrCodeImage)

        // Set a click listener on the profile icon to navigate to the student's profile
        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this@QrCodePageActivity, ProfileStudActivity::class.java))
        }

        // Launch a coroutine to generate and display the QR code asynchronously
        CoroutineScope(Dispatchers.Main).launch {
            qrCodeImage.setImageBitmap(QrCodeApiService.generateQrCode())
        }

        // Start the countdown timer for the QR code refresh
        startCountdownTimer()
    }

    // Function to start the countdown timer
    private fun startCountdownTimer() {
        val timerDuration = 30000L // Set the timer duration to 30 seconds

        // Create and start a new countdown timer
        object : CountDownTimer(timerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the progress bar every second (1 second = 1000 milliseconds)
                val progress = (millisUntilFinished / 1000).toInt()
                progressBar.progress = progress
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                // Reset the progress bar to 0 when the timer finishes
                progressBar.progress = 0

                // Launch a coroutine to generate and display a new QR code
                CoroutineScope(Dispatchers.Main).launch {
                    qrCodeImage.setImageBitmap(QrCodeApiService.generateQrCode())
                }

                startCountdownTimer()
            }
        }.start() // Start the timer
    }
}
