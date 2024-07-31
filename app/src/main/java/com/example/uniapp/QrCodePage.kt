package com.example.uniapp

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
//import com.bumptech.glide.Glide


class QrCodePage {

    class MainActivity : AppCompatActivity() {

        private lateinit var progressBar: ProgressBar
        private lateinit var qrCodeImage: ImageView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.qr_page)

            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)

            progressBar = findViewById(R.id.timerProgressBar)
            qrCodeImage = findViewById(R.id.qrCodeImage)

            // Load the unicorn image
            //val unicornImage: ImageView = findViewById(R.id.unicorn_image_stud)
            //Glide.with(this).load(R.drawable.unicorno_prova_stud).into(unicornImage)

            // Load the QR code image from server
            //val qrCodeUrl = "https://yourserver.com/qrcode.png"
            //Glide.with(this).load(qrCodeUrl).into(qrCodeImage)

            // Start the countdown timer
            startCountdownTimer()
        }

        private fun startCountdownTimer() {
            val timerDuration = 30000L // 30 seconds

            object : CountDownTimer(timerDuration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val progress = (millisUntilFinished / 1000).toInt()
                    progressBar.progress = progress
                }

                override fun onFinish() {
                    progressBar.progress = 0
                    // Optionally, restart the timer or handle completion
                    startCountdownTimer()
                }
            }.start()
        }
    }

}