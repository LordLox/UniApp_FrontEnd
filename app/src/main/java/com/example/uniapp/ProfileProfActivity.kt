package com.example.uniapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.uniapp.network.LoginApiService
import com.example.uniapp.util.FileStorageUtils
import com.example.uniapp.util.GlobalUtils
import com.example.uniapp.util.NavigationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileProfActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        NavigationUtils.returnToLoginIfNotLogged(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_prof)

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide default title

        // Handle back button click
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Finish the activity to go back
        }

        // Handle change password button click
        val changePasswordButton = findViewById<Button>(R.id.change_password_button)
        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        val usernameField = findViewById<TextView>(R.id.usernameField)
        usernameField.text = GlobalUtils.userInfo.username

        val nameField = findViewById<TextView>(R.id.nameField)
        nameField.text = GlobalUtils.userInfo.name

        // Handle logout button click
        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            FileStorageUtils.deleteFile(GlobalUtils.userInfoFileName)
            startActivity(Intent(this@ProfileProfActivity, MainActivity::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showChangePasswordDialog() {
        // Create an EditText for password input
        val passwordInput = EditText(this).apply {
            hint = "Enter new password"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        var changePasswordResponse: String?

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setMessage("Please enter your new password")
            .setView(passwordInput)
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                val newPassword = passwordInput.text.toString()
                if (newPassword.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        changePasswordResponse = LoginApiService.changePassword(newPassword)
                            .replace("\"", "")
                            .replace("\\n", "\n")
                        if(changePasswordResponse != ""){
                            // Create the AlertDialog
                            val responseDialog = AlertDialog.Builder(this@ProfileProfActivity)
                                .setTitle("Error: Unable to change password")
                                .setMessage(changePasswordResponse)
                                .setNegativeButton("Ok", null)
                                .create()
                            responseDialog.show()
                        }
                        else
                        {
                            // Create the AlertDialog
                            val responseDialog = AlertDialog.Builder(this@ProfileProfActivity)
                                .setTitle("Succesfully changed password")
                                .setMessage("Now you need to login again to continue using the app")
                                .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                                    FileStorageUtils.deleteFile(GlobalUtils.userInfoFileName)
                                    startActivity(Intent(this@ProfileProfActivity, MainActivity::class.java))
                                }
                                .create()
                            responseDialog.show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        // Show the dialog
        dialog.show()
    }
}
