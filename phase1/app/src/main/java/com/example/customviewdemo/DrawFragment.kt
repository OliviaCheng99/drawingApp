package com.example.customviewdemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.customviewdemo.databinding.FragmentDrawBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DrawFragment : Fragment() {

    private val viewModel: SimpleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDrawBinding.inflate(inflater)

        // setup touch listener
        setupTouchListener(binding)

        // clear previous points
        viewModel.points.value?.clear()

        // observe the points
        viewModel.points.observe(viewLifecycleOwner) { myPoints ->
            binding.customView.drawPoints(myPoints)
        }

        // Set up the Save Drawing button
        binding.saveDrawingButton.setOnClickListener {
            // Generate a unique filename for each drawing, or use a naming convention of your choice
            val filename = "drawing_${System.currentTimeMillis()}.png"
            val filePath = binding.customView.saveDrawingToInternalStorage(filename)

            if (filePath != null) {
                Toast.makeText(requireContext(), "Drawing saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error saving drawing!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener(binding: FragmentDrawBinding) {
        binding.customView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val point = PointF(event.x, event.y)
                    val color = toColor(viewModel.getColor())
                    val size = viewModel.getSize().toFloat()
                    val shape = viewModel.getShape()

                    viewModel.addPoint(point, color, size, shape)
                    true
                }
                else -> false
            }
        }
    }


    private fun toColor(colorName: String): Int {
        return when (colorName) {
            "Red" -> Color.RED
            "Blue" -> Color.BLUE
            "Green" -> Color.GREEN
            else -> Color.BLACK
        }
    }
}
