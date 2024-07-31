package com.example.uniapp
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class LoginScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        val usernameEditText: EditText = findViewById(R.id.usernameField)
        val passwordEditText: EditText = findViewById(R.id.passwordField)
        val loginButton: Button = findViewById(R.id.loginButton)

        // Example AES key and IV
        val aesKey = SecretKeySpec("qwr{@^h`h&_`50/ja9!'dcmh3!uw<&=?".toByteArray(), "AES")
        val iv = IvParameterSpec("your-iv-12345678".toByteArray())

        val loginManager = LoginManager(this)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginManager.login(username, password, aesKey, iv)
        }
    }
}
