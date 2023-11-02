package com.example.drawApp

import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.http.isSuccess
import java.io.IOException

class MomentsRepository {

    // Implement functions to get shared images and delete images using Ktor

    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun getSharedImages(): List<ByteArray> {
        val response = KtorHttpClient.httpClient.get("$baseUrl/images")
        if (response.status.isSuccess()) {
            // Parse the response to obtain a list of shared image URLs
            val imageUrls = response.body<List<String>>()
            val images = mutableListOf<ByteArray>()
            for (imageUrl in imageUrls) {
                val imageResponse = KtorHttpClient.httpClient.get(imageUrl)
                if (imageResponse.status.isSuccess()) {
                    images.add(imageResponse.readBytes())
                }
            }
            return images
        } else {
            throw IOException("Failed to fetch shared images from the server")
        }
    }

    suspend fun deleteImage(imageId: String) {
        val response = KtorHttpClient.httpClient.delete("$baseUrl/images/$imageId")
        if (!response.status.isSuccess()) {
            throw IOException("Failed to delete the image from the server")
        }
    }
}