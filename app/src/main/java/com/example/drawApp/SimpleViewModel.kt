package com.example.drawApp


import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


data class MyPoint(
    val point: PointF,
    val color: Int,
    val size: Float,
    val shape: String
)

class SimpleViewModel(private val repo: DrawingRepo) :ViewModel() {

    val allDrawings : LiveData<List<DrawData>> = repo.allDrawings

    private val _points = MutableLiveData<MutableList<MyPoint>>().apply {
        value = mutableListOf()
    }
    val points = _points as LiveData<MutableList<MyPoint>>

    private var selectedColor = "Red" // red as default

    private var selectedSize = "5"

    private var selectedShape = "Circle"

    fun addPoint(point: PointF, color: Int, size: Float, shape: String) {
        _points.value?.add(MyPoint(point, color, size, shape))
        _points.value = _points.value // call set value just to notify
    }

    fun setColor(color: String) {
        selectedColor = color
        Log.d("SimpleViewModel", "setColor called with value: $color")
    }

    fun setSize(size: String){
        selectedSize= size
    }

    fun setShape(shape:String){
        selectedShape = shape
    }

    fun getColor() : String{
        return selectedColor
    }

    fun getSize() : String{
        return selectedSize
    }

    fun getShape() : String{
        return selectedShape
    }

    fun addDrawing(fileName: String, filePath: String){
        repo.addDrawing(fileName, filePath)
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