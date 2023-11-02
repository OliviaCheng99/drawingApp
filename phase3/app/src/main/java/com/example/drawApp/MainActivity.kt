package com.example.drawApp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.readText
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException



class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //get navcontroller instance
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        // set up the bottom bar navigation
        navView = findViewById(R.id.bottom_nav)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.canvas -> {
                    navController.navigate(R.id.drawFragment)
                    true
                }

                R.id.savings -> {
                    navController.navigate(R.id.drawingListFragment)
                    true
                }
                R.id.moments->{
                    navController.navigate(R.id.momentsFragment)
                    true
                }
                else -> false
            }
        }
    }

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}

