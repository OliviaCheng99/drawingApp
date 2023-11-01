package com.example.drawApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // set up the bottom bar navigation
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.canvas ->{
                    findNavController(R.id.fragmentContainerView).navigate(R.id.drawFragment)
                    true
                }

                R.id.savings -> {
                    findNavController(R.id.fragmentContainerView).navigate(R.id.drawingListFragment)
                    true
                }

                else -> false
            }
        }
    }
}

