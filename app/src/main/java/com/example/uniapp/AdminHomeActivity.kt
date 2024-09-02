package com.example.uniapp
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.uniapp.util.NavigationUtils

class AdminHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NavigationUtils.returnToLoginIfNotLogged(this)
        setContentView(R.layout.admin_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)

        findViewById<Button>(R.id.create_user).setOnClickListener {
        }

        findViewById<Button>(R.id.update_user).setOnClickListener {
        }

        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
        }
    }
}
