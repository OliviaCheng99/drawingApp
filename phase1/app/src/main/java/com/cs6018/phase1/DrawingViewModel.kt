package com.cs6018.phase1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawingViewModel : ViewModel() {
    val paths = MutableLiveData<MutableList<DrawPath>>().apply {
        value = mutableListOf()
    }

    fun addPath(drawPath: DrawPath) {
        paths.value?.add(drawPath)
        paths.postValue(paths.value)
    }
}
