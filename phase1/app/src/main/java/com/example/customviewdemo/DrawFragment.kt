package com.example.customviewdemo

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.customviewdemo.databinding.FragmentDrawBinding


class DrawFragment : Fragment() {

    private val viewModel: SimpleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDrawBinding.inflate(inflater)

        // setup touch listener
        setupTouchListener(binding)

        // observe the points
        viewModel.points.observe(viewLifecycleOwner) { myPoints ->
            binding.customView.drawPoints(myPoints)
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





