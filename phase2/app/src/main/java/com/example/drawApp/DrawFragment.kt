package com.example.drawApp

import android.annotation.SuppressLint
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.drawApp.databinding.FragmentDrawBinding


class DrawFragment : Fragment() {

    private val viewModel: SimpleViewModel by activityViewModels{
        SimpleViewModelFactory((requireContext().applicationContext as DrawingApplication).drawingRepo)}

    private lateinit var binding: FragmentDrawBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrawBinding.inflate(inflater)

        // setup touch listener
        setupTouchListener(binding)

        // make sure that all fragments use the same viewmodel
//        Log.d("ClickFragment", "ViewModel hashcode: ${viewModel.hashCode()}


        binding.options.setContent {
            val color by viewModel.selectedColor.observeAsState()
            val stroke by viewModel.strokeWidth.observeAsState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                ColorPicker(myColor = Color(color!!))
                StrokePicker(myStroke = stroke!!)
            }
        }

        binding.saveDrawingButton.setContent {
            SaveButton{
                // Generate a unique filename for each drawing, or use a naming convention of your choice
                val filename = "drawing_${System.currentTimeMillis()}.png"
                // add drawing to local storage
                val filePath = binding.customView.saveDrawingToInternalStorage(filename)

                if (filePath != null) {
                    // push filename and file path to database
                    viewModel.addDrawingToDB(filename, filePath)
                    Toast.makeText(requireContext(), "Drawing saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error saving drawing!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // observe the path
        viewModel.paths.observe(viewLifecycleOwner) { paths ->
            binding.customView.drawPaths(paths)
        }

        return binding.root
    }

    override fun onResume() { // when fragment recover, force the view to render
        super.onResume()
        binding.customView.invalidate()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener(binding: FragmentDrawBinding) {
        binding.customView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val newPath = Path().apply { moveTo(event.x, event.y) }
                    viewModel.addPath(newPath)
                }

                MotionEvent.ACTION_MOVE -> {
                    viewModel.paths.value!!.last().path.lineTo(event.x, event.y)
                    viewModel.updatePath()
                }
                else -> false
            }
                true
            }
        }


    @Composable
    fun ColorPicker(myColor: Color?) {
        // Sample colors for the picker
        val colors = listOf(Color.Red,Color.Blue,Color.Green,Color.Black)

        Row(
            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color)
                        .clickable {viewModel.updateColor(color.toArgb())}
                        .border(
                            5.dp,
                            if (myColor == color) Color.DarkGray else Color.Transparent
                        )
                )
            }
            MyEraser() //
        }
    }

    @Composable
    fun StrokePicker(myStroke: String) {
        OutlinedTextField(
            value = myStroke,
            onValueChange = { viewModel.updateStroke(it) },
            label = { Text(text = "Stroke Width") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            modifier = Modifier.width(100.dp)
        )

    }

    @Composable
    fun MyEraser(){
        Box(
            modifier = Modifier
                .size(50.dp)
                //.background(if (isEraserEnabled) Color.Gray else Color.Transparent) // Highlight when eraser is enabled
                //.clickable { viewModel.isEraserEnabled.value = !isEraserEnabled }
                .clickable { viewModel.updateColor(Color.White.toArgb()) }
                .padding(8.dp) // Optional padding for the icon
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_erasers),
                contentDescription = "Eraser"
            )
        }
    }

    @Composable
    fun SaveButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
        ) {
            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Save Image")
            Text(text = "Save Image")
        }
    }

}


//    private fun toColor(colorName: String): Int {
//        return when (colorName) {
//            "Red" -> Color.RED
//            "Blue" -> Color.BLUE
//            "Green" -> Color.GREEN
//            else -> Color.BLACK
//        }
//    }


//
//    @SuppressLint("ClickableViewAccessibility")
//    private fun setupTouchListener(binding: FragmentDrawBinding) {
//        binding.customView.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
//                    val point = PointF(event.x, event.y)
//                    val color = toColor(viewModel.getColor())
//                    val size = viewModel.getSize().toFloat()
//                    val shape = viewModel.getShape()
//
//                    viewModel.addPoint(point, color, size, shape)
//                    true
//                }
//                else -> false
//            }
//        }
//    }


