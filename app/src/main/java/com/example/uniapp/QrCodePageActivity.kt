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

    private lateinit var progressBar: ProgressBar
    private lateinit var qrCodeImage: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        NavigationUtils.returnToLoginIfNotLogged(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_page)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide default title

        progressBar = findViewById(R.id.timerProgressBar)
        qrCodeImage = findViewById(R.id.qrCodeImage)

        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this@QrCodePageActivity, ProfileStudActivity::class.java))
        }

        CoroutineScope(Dispatchers.Main).launch {
            qrCodeImage.setImageBitmap(QrCodeApiService.generateQrCode())
        }

        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        val timerDuration = 30000L // 30 seconds

        object : CountDownTimer(timerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = (millisUntilFinished / 1000).toInt()
                progressBar.progress = progress
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                progressBar.progress = 0

                // Download NewQrCode
                CoroutineScope(Dispatchers.Main).launch {
                    qrCodeImage.setImageBitmap(QrCodeApiService.generateQrCode())
                }

                // Optionally, restart the timer or handle completion
                startCountdownTimer()
            }
        }.start()
    }
}
