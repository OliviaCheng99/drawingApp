package com.example.drawApp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MomentsViewModel : ViewModel() {
    private val momentsRepository = MomentsRepository()

    val sharedImages: MutableLiveData<List<ByteArray>> = MutableLiveData()

    suspend fun loadSharedImages() {
        // Call a function in the repository to get shared images from the server
        // Update sharedImages LiveData with the obtained images
//        sharedImages.postValue(momentsRepository.getSharedImages())
        viewModelScope.launch {
            try {
                val images = momentsRepository.getSharedImages()
                sharedImages.postValue(images)
            } catch (e: Exception) {
                // Handle the error, e.g., show a toast or log an error message
            }
        }
    }

    fun deleteImage(imageId: String) {
        // Call a function in the repository to delete the image by its ID
        viewModelScope.launch {
            try {
                momentsRepository.deleteImage(imageId)
                // Reload shared images after a successful deletion
                loadSharedImages()
            } catch (e: Exception) {
                // Handle the error, e.g., show a toast or log an error message
            }
        }
    }
}
