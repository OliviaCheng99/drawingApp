package com.example.drawApp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Path
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.drawApp.databinding.FragmentDrawBinding
import kotlinx.coroutines.launch


class DrawFragment : Fragment() {

    private val viewModel: SimpleViewModel by activityViewModels {
        SimpleViewModelFactory((requireContext().applicationContext as DrawingApplication).drawingRepo)
    }


    private lateinit var binding: FragmentDrawBinding

    private val pickImageResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val imageStream = context?.contentResolver?.openInputStream(it)
                val selectedImageBitmap = BitmapFactory.decodeStream(imageStream)
                binding.customView.setImageBitmap(selectedImageBitmap)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrawBinding.inflate(inflater)

        // setup touch listener
        setupTouchListener(binding)

        binding.options.setContent {
            val sensorManager =
                requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val marblePosition: MutableState<Offset> = remember { mutableStateOf(Offset(0f, 0f)) }
            var isGravityMode by remember { mutableStateOf(false) }
            var gravityEventListener: SensorEventListener? by remember { mutableStateOf(null) }

            val color by viewModel.selectedColor.observeAsState()
            val stroke by viewModel.strokeWidth.observeAsState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColorPicker(myColor = Color(color!!))
                SwitchDrawingModeButton(
                    isGravityMode = isGravityMode,
                    onSwitchMode = {
                        isGravityMode = !isGravityMode
                        if (isGravityMode) {
                            gravityEventListener =
                                setupGravitySensorForDrawing(
                                    sensorManager = sensorManager,
                                    marblePosition = marblePosition)
                        } else {
                            sensorManager.unregisterListener(gravityEventListener)
                        }
                    },
                )
                StrokePicker(myStroke = stroke!!)
            }
        }

        binding.bottomButtonList.setContent {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var isSaveDialogVisible by remember { mutableStateOf(false) }
                    var imageNameToSave by remember { mutableStateOf("") }

                    if (isSaveDialogVisible) {
                        SaveImageDialog(
                            onDismiss = { isSaveDialogVisible = false },
                            onConfirm = { name ->
                                if (name.isNotEmpty()) {
                                    val filePath =
                                        binding.customView.saveDrawingToInternalStorage("$name.png")
                                    if (filePath != null) {
                                        viewModel.addDrawingToDB("$name.png", filePath)
                                    }
                                    isSaveDialogVisible = false
                                    Toast.makeText(
                                        requireContext(),
                                        "Saved Successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // handle invalid name case
                                    Toast.makeText(
                                        requireContext(),
                                        "Please type something!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            name = imageNameToSave,
                            onImageNameChange = { imageNameToSave = it }
                        )
                    }

                    SaveButton {
                        isSaveDialogVisible = true
                    }
                    ClearButton {
                        binding.customView.clearDrawing()
                        viewModel.clearPath()
                    }
                    MoreButton()
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
            }
            true
        }
    }

    private fun setupGravitySensorForDrawing(
        marblePosition: MutableState<Offset>,
        sensorManager: SensorManager
    ): SensorEventListener {
        // Set up the gravity sensor for drawing

        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val gravityEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                val maxW = binding.customView.width.toFloat()
                val maxH = binding.customView.height.toFloat()

                if (viewModel.paths.value?.isEmpty()!!) {
                    marblePosition.value = marblePosition.value.copy(
                        maxW / 2,
                        maxH / 2
                    )
                    val newPath = Path().apply { moveTo(maxW / 2, maxH / 2) }
                    viewModel.addPath(newPath)
                }

                val x = event.values[0]
                val y = event.values[1]

                val deltaX = mapSensorValueToDelta(-x) // Note: x is negated here
                val deltaY = mapSensorValueToDelta(y)

                marblePosition.value = marblePosition.value.copy(
                    x = (marblePosition.value.x + deltaX).coerceIn(0F, maxW),
                    y = (marblePosition.value.y + deltaY).coerceIn(0F, maxH)
                )

                viewModel.paths.value!!.last().path.lineTo(marblePosition.value.x, marblePosition.value.y)
                viewModel.updatePath()
            }
        }

        sensorManager.registerListener(
            gravityEventListener,
            gravitySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        return gravityEventListener
    }

    fun mapSensorValueToDelta(sensorValue: Float): Float {
        val multiplier = .5f // Adjust this for sensitivity
        return sensorValue * multiplier
    }


    @Composable
    fun ColorPicker(myColor: Color?) {
        // Sample colors for the picker
        val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Black)

        Row(
            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color)
                        .clickable { viewModel.updateColor(color.toArgb()) }
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
    fun MyEraser() {
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
            Text(text = "Save")
        }
    }

    @Composable
    fun ClearButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
        ) {
            Text(text = "Clear")
        }
    }


    @Composable
    fun MoreButton() {
        var showMenu by remember { mutableStateOf(false) }

        // if true, show menu
        if (showMenu) {
            CustomMenu(onDismiss = {
                // when meue disapears, update state
                showMenu = false
            })
        }

        Button(onClick = { showMenu = true }) {
            Text("More")
        }
    }

    @Composable
    fun CustomMenu(onDismiss: () -> Unit) {
        // status，track whether the menu is opened
        var expanded by remember { mutableStateOf(true) }

        var isLoadDialogVisible by remember { mutableStateOf(false) }
        var imageNameToLoad by remember { mutableStateOf("") }
        val allDrawingData by viewModel.allDrawings.observeAsState()
        val fileNames: List<String>? = allDrawingData?.map { it.filename }


        if (isLoadDialogVisible) {
            LoadImageDialog(
                onDismiss = { isLoadDialogVisible = false },
                onConfirm = { name ->
                    if (name.isNotEmpty()) {
                        val loadedBitmap = loadSingleSavedDrawing(fileNames, "$name.png")
                        if (loadedBitmap != null) {
                            binding.customView.setImageBitmap(loadedBitmap)
                        } else {
                            Toast.makeText(requireContext(), "Not Found!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        // handle invalid name case
                        Toast.makeText(
                            requireContext(),
                            "Please type something!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                name = imageNameToLoad,
                onImageNameChange = { imageNameToLoad = it }
            )
        }


        Column {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Open menu",
                modifier = Modifier.clickable { expanded = !expanded }
            )

            // show menu based on status
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    onDismiss()
                }
            ) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    viewLifecycleOwner.lifecycleScope.launch {
                        binding.customView.saveBitmapToGallery(requireContext())
                    }
                }) {
                    Text("Save to Gallery")
                }

                DropdownMenuItem(onClick = {
                    expanded = false
                    viewModel.clearPath()
                    try {
                        pickImageResult.launch("image/*")
                    } catch (e: Exception) {
                        Log.e("custom view", "error setting images", e)
                    }
                }) {
                    Text("Import From Gallery")
                }

                DropdownMenuItem(onClick = {
                    expanded = false
                    viewModel.clearPath()
                    isLoadDialogVisible = true
                }) {
                    Text("Import From APP")
                }

                DropdownMenuItem(onClick = {
                    expanded = false
                    viewLifecycleOwner.lifecycleScope.launch {// maybe not necessary to handle it in coroutine here
                        binding.customView.shareBitmap(requireContext())
                    }
                }) {
                    Text("Share Images")
                }
            }
        }
    }

    @Composable
    fun SaveImageDialog(
        onDismiss: () -> Unit,
        onConfirm: (String) -> Unit,
        name: String,
        onImageNameChange: (String) -> Unit
    ) {
        AlertDialog(
            title = { Text(text = "Save Image") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = onImageNameChange,
                    label = { Text("Enter image name") }
                )
            },
            confirmButton = {
                Button(onClick = { onConfirm(name) }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            onDismissRequest = onDismiss
        )
    }

    @Composable
    fun LoadImageDialog(
        onDismiss: () -> Unit,
        onConfirm: (String) -> Unit,
        name: String,
        onImageNameChange: (String) -> Unit
    ) {
        AlertDialog(
            title = { Text(text = "Load Image") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = onImageNameChange,
                    label = { Text("Enter image name") }
                )
            },
            confirmButton = {
                Button(onClick = { onConfirm(name) }) {
                    Text("Load")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            onDismissRequest = onDismiss
        )
    }

    private fun loadSingleSavedDrawing(fileNames: List<String>?, target: String): Bitmap? {
        if (!fileNames.isNullOrEmpty()) {
            for (filename in fileNames) {
                if (filename == target) {
                    val fis = context?.openFileInput(filename)
                    fis?.use {
                        return BitmapFactory.decodeStream(it)
                    }
                }
            }
        }
        return null
    }

    @Composable
    fun SwitchDrawingModeButton(
        isGravityMode: Boolean,
        onSwitchMode: () -> Unit,
    ) {
        Box(
            modifier = Modifier
                .clickable {
                    onSwitchMode()
                }
        ) {
            Icon(
                imageVector = if (isGravityMode) Icons.Default.LocationOn else Icons.Default.Edit,
                contentDescription = "SwitchDrawingModeButton"
            )
        }
    }


}


