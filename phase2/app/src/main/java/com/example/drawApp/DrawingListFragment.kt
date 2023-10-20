package com.example.drawApp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels



class DrawingListFragment : Fragment() {

    private val viewModel: SimpleViewModel by activityViewModels{
        SimpleViewModelFactory((requireContext().applicationContext as DrawingApplication).drawingRepo)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply{
            setContent {
                MaterialTheme{
                    Column {
                        val allDrawingData by viewModel.allDrawings.observeAsState()
                        val fileNames: List<String>? = allDrawingData?.map { it.filename }
                        Log.e("filenames", fileNames.toString() )
                        Text("The Previous Drawings")
                        DrawingsList {
                            loadSavedDrawings(fileNames)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DrawingsList(callback: ()-> List<Bitmap>) {
        val drawings = callback()
        LazyColumn {
            items(drawings) { drawing ->
                Image(
                    modifier = Modifier.padding(4.dp),
                    bitmap = drawing.asImageBitmap(),
                    contentDescription = null
                )
            }
        }
    }


    private fun loadSavedDrawings(fileNames: List<String>?): List<Bitmap> {
        val bitmaps: MutableList<Bitmap> = mutableListOf()
        if (fileNames != null && fileNames.isNotEmpty()) {
            for (filename in fileNames) {
                if (filename.endsWith(".png")) { // Only process PNG files
                    val fis = context?.openFileInput(filename)
                    fis?.use {
                        val bitmap = BitmapFactory.decodeStream(it)
                        bitmaps.add(bitmap)
                    }
                }
            }
        }
        return bitmaps
    }
}
