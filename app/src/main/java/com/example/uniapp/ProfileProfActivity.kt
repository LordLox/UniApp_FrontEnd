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
        // Redirect to the login screen if the user is not logged in
        NavigationUtils.returnToLoginIfNotLogged(this)

        // Call the superclass's onCreate method to perform standard initialization
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_prof)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide the default title

        // Handle the back button click to finish the activity and return to the previous screen
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Finish the activity to go back
        }

        // Handle the change password button click
        val changePasswordButton = findViewById<Button>(R.id.change_password_button)
        changePasswordButton.setOnClickListener {
            showChangePasswordDialog() // Show the dialog to change the password
        }

        // Display the user's username and name in the respective fields
        val usernameField = findViewById<TextView>(R.id.usernameField)
        usernameField.text = GlobalUtils.userInfo.username

        val nameField = findViewById<TextView>(R.id.nameField)
        nameField.text = GlobalUtils.userInfo.name

        // Handle the logout button click
        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            // Delete the user info file and navigate to the main activity (which checks login status)
            FileStorageUtils.deleteFile(GlobalUtils.applicationPath, GlobalUtils.userInfoFileName)
            startActivity(Intent(this@ProfileProfActivity, MainActivity::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showChangePasswordDialog() {
        // Create an EditText for password input
        val passwordInput = EditText(this).apply {
            hint = "Enter new password"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // Set input type to password
        }

        var changePasswordResponse: String?

        // Create the AlertDialog for changing the password
        val dialog = AlertDialog.Builder(this)
            .setTitle("Change Password") // Title of the dialog
            .setMessage("Please enter your new password") // Message in the dialog
            .setView(passwordInput) // Set the password input field in the dialog
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                val newPassword = passwordInput.text.toString()
                if (newPassword.isNotEmpty()) {
                    // Launch a coroutine to change the password asynchronously
                    CoroutineScope(Dispatchers.Main).launch {
                        changePasswordResponse = LoginApiService.changePassword(newPassword)
                            .replace("\"", "") // Remove extra quotes from the response
                            .replace("\\n", "\n") // Format the newline characters

                        if (changePasswordResponse != "") {
                            // Show an error dialog if the password change failed
                            val responseDialog = AlertDialog.Builder(this@ProfileProfActivity)
                                .setTitle("Error: Unable to change password")
                                .setMessage(changePasswordResponse)
                                .setNegativeButton("Ok", null)
                                .create()
                            responseDialog.show()
                        } else {
                            // Show a success dialog if the password change was successful
                            val responseDialog = AlertDialog.Builder(this@ProfileProfActivity)
                                .setTitle("Successfully changed password")
                                .setMessage("Now you need to log in again to continue using the app")
                                .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                                    // Delete the user info file and navigate to the main activity to re-login
                                    FileStorageUtils.deleteFile(GlobalUtils.applicationPath, GlobalUtils.userInfoFileName)
                                    startActivity(Intent(this@ProfileProfActivity, MainActivity::class.java))
                                }
                                .create()
                            responseDialog.show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null) // Set a Cancel button to dismiss the dialog
            .create()

        // Show the change password dialog
        dialog.show()
    }
}
