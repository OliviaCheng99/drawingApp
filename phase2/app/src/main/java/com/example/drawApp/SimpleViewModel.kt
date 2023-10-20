package com.example.drawApp


import android.graphics.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


data class DrawingPath (
    val path: Path,
    val color: Int,
    val strokeWidth: Float,
)

class SimpleViewModel(private val repo: DrawingRepo) :ViewModel() {

    val allDrawings : LiveData<List<DrawData>> = repo.allDrawings

    private val _paths = MutableLiveData(mutableListOf<DrawingPath>())
    val paths = _paths as LiveData<MutableList<DrawingPath>>

    private var _selectedColor = MutableLiveData(Color.Black.toArgb())
    val selectedColor = _selectedColor as LiveData<Int>

    private var _strokeWidth = MutableLiveData("10")
    val strokeWidth = _strokeWidth as LiveData<String>

    fun addDrawingToDB(fileName: String, filePath: String){
        repo.addDrawingToDB(fileName, filePath)
    }

    fun addPath(newPath: Path) {
        _paths.value?.add(DrawingPath(newPath, _selectedColor.value!!, _strokeWidth.value!!.toFloat()))
        _paths.value = _paths.value
    }

    fun updateColor(color: Int) {
        _selectedColor.value = color
    }

    fun updateStroke(strokeWidth: String) {
        _strokeWidth.value = strokeWidth
    }

    fun updatePath() {
        _paths.value = _paths.value
    }

}

// This factory class allows us to define custom constructors for the view model
class SimpleViewModelFactory(private val repository: DrawingRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SimpleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SimpleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


//data class MyPoint(
//    val point: PointF,
//    val color: Int,
//    val size: Float,
//    val shape: String
//)


//    private val _points = MutableLiveData<MutableList<MyPoint>>().apply {
//        value = mutableListOf()
//    }
//    val points = _points as LiveData<MutableList<MyPoint>>
//
//    private var selectedColor = "Red" // red as default
//
//    private var selectedSize = "5"
//
//    private var selectedShape = "Circle"

//
//    fun addPoint(point: PointF, color: Int, size: Float, shape: String) {
//        _points.value?.add(MyPoint(point, color, size, shape))
//        _points.value = _points.value // call set value just to notify
//    }