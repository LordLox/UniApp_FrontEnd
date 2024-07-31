package com.example.uniapp
import android.content.Context
import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class LoginManager(private val context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://your-api-url.com/")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun login(username: String, password: String, aesKey: SecretKey, iv: IvParameterSpec) {
        val request = LoginRequest(username, password)
        val call = apiService.login(request)

        call.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: retrofit2.Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val encryptedInfo = response.body()?.encryptedUserInfo
                    encryptedInfo?.let {
                        saveToFile(it)
                        val decryptedInfo = decrypt(it, aesKey, iv)
                        // Use the decrypted info as needed
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun saveToFile(data: String) {
        val file = File(context.filesDir, "user_info.enc")
        file.writeText(data)
    }

    private fun decrypt(encryptedData: String, secretKey: SecretKey, iv: IvParameterSpec): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val decodedValue = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedValue = cipher.doFinal(decodedValue)
        return String(decryptedValue)
    }
}
