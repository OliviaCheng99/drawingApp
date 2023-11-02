package com.example.drawApp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // set up the bottom bar navigation
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.canvas -> {
                    findNavController(R.id.fragmentContainerView).navigate(R.id.drawFragment)
                    true
                }

                R.id.savings -> {
                    findNavController(R.id.fragmentContainerView).navigate(R.id.drawingListFragment)
                    true
                }
                R.id.moments->{
                    findNavController(R.id.fragmentContainerView).navigate(R.id.momentsFragment)
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

