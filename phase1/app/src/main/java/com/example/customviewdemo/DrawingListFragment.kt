package com.example.customviewdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DrawingListFragment : Fragment() {

    private lateinit var drawingsAdapter: DrawingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drawing_list, container, false)

        val drawings = loadSavedDrawings()

        drawingsAdapter = DrawingsAdapter(drawings)

        val recyclerView = view.findViewById<RecyclerView>(R.id.drawingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = drawingsAdapter

        return view
    }

    private fun loadSavedDrawings(): List<Bitmap> {
        val bitmaps: MutableList<Bitmap> = mutableListOf()

        // Retrieve saved drawing filenames
        val files = context?.fileList()

        if (files != null && files.isNotEmpty()) {
            for (filename in files) {
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
