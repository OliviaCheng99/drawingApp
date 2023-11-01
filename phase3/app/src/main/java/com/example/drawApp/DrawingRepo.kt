package com.example.drawApp

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date


class DrawingRepo(val scope: CoroutineScope, val dao: DrawDao): Repo {
    override val allDrawings = dao.getAllDrawings().asLiveData()
    override fun addDrawingToDB(filename: String, filepath: String){
        scope.launch {
            dao.addDrawData(
                DrawData(timestamp = Date(), filepath = filepath, filename = filename)
            )
        }

    }
}




