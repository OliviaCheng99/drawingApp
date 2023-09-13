package com.example.customviewdemo


import android.graphics.PointF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


data class MyPoint(
    val point: PointF,
    val color: Int,
    val size: Float,
    val shape: String
)


class SimpleViewModel :ViewModel() {

    private val _points = MutableLiveData<MutableList<MyPoint>>().apply {
        value = mutableListOf()
    }
    val points = _points as LiveData<MutableList<MyPoint>>

    private val _selectedColor = MutableLiveData<String>("Red") // red as default
    val selectedColor = _selectedColor as LiveData<String>

    private val _selectedSize = MutableLiveData<String>("5")
    val selectedSize = _selectedSize as LiveData<String>

    private val _selectedShape = MutableLiveData<String>("Circle")
    val selectedShape = _selectedShape as LiveData<String>

    fun addPoint(point: PointF, color: Int, size: Float, shape: String) {
        _points.value?.add(MyPoint(point, color, size, shape))
        _points.postValue(_points.value)
    }


    fun selectColor(color: String) {
        _selectedColor.value = color
    }

    fun selectSize(size: String){
        _selectedSize.value = size
    }

    fun selectShape(shape:String){
        _selectedShape.value = shape
    }


}