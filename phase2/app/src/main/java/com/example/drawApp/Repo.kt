package com.example.drawApp

import androidx.lifecycle.LiveData
//add this interface to mock call DrawingRepo because it's not easy to directly mock a class
interface Repo {
    val allDrawings: LiveData<List<DrawData>>
    fun addDrawingToDB(filename: String, filepath: String)
}