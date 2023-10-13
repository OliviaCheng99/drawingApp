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

    private var selectedColor = "Red" // red as default

    private var selectedSize = "5"

    private var selectedShape = "Circle"

    fun addPoint(point: PointF, color: Int, size: Float, shape: String) {
        _points.value?.add(MyPoint(point, color, size, shape))
        _points.value = _points.value // call set value just to notify
    }

    fun setColor(color: String) {
        selectedColor = color
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

}