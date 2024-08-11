package com.example.uniapp
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.uniapp.util.GlobalUtils
import com.example.uniapp.util.NavigationUtils

class HomepageProfessor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        NavigationUtils.returnToLoginIfNotLogged(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage_professor)

        GlobalUtils.checkCameraPermission(this)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up button click listeners (optional)
        findViewById<Button>(R.id.read_qr_code_button).setOnClickListener {
            startActivity(Intent(this@HomepageProfessor, ReadQrPage::class.java))
        }

        findViewById<Button>(R.id.create_event_button).setOnClickListener {
            // Handle Create com.example.uniapp.Event button click
        }

        findViewById<Button>(R.id.event_list_button).setOnClickListener {
            startActivity(Intent(this@HomepageProfessor, EventList::class.java))
        }

        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this@HomepageProfessor, ProfileProf::class.java))
        }
    }
}
