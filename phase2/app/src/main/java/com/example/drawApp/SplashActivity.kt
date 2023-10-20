package com.example.drawApp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            // Navigate to main activity or any other activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)  // Display splash for 2 seconds
    }
}
