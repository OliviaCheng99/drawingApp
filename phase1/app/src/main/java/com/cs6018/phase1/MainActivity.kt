package com.cs6018.phase1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: DrawingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(DrawingViewModel::class.java)

        val drawingView = findViewById<DrawingView>(R.id.drawingView)

        // Color Buttons
        findViewById<Button>(R.id.btnBlack).setOnClickListener { drawingView.setColor(Color.BLACK) }
        findViewById<Button>(R.id.btnRed).setOnClickListener { drawingView.setColor(Color.RED) }
        findViewById<Button>(R.id.btnBlue).setOnClickListener { drawingView.setColor(Color.BLUE) }

        // Brush Size Buttons
        findViewById<Button>(R.id.btnSmallBrush).setOnClickListener { drawingView.setStrokeWidth(5f) }
        findViewById<Button>(R.id.btnMediumBrush).setOnClickListener { drawingView.setStrokeWidth(15f) }
        findViewById<Button>(R.id.btnLargeBrush).setOnClickListener { drawingView.setStrokeWidth(30f) }
    }
}

