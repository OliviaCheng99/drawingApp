package com.example.drawApp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.InternalAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ResponseData(
    val id: Int,
    val userId: String,
    val time: Long, // post time
    val imageBytes: String
)

class MomentsFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply{
            setContent {
                MaterialTheme{
                    Text(text = "moments")
                    ImageList()
                }
            }
        }

    }

    @Composable
    fun ImageList() {
        var imageList by remember { mutableStateOf(emptyList<ResponseData>()) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) { // This will launch when the composable is first shown
            while (true) { // This will create an infinite loop; bad practice?
                scope.launch {
                    try {
                        val newImages = getResponseData()
                        imageList = newImages
                    } catch (e: Exception) {
                        // Handle any exceptions that might arise from the network call
                        Log.e("network", e.toString())
                    }
                }
                delay(3000) // Delay for 3 seconds before the next fetch
            }
        }

        LazyColumn {
            items(imageList) { imgData ->
                ImageItem(imageData = imgData, currentUser = "user123")
            }
        }
    }

    @Composable
    fun ImageItem(imageData: ResponseData, currentUser: String) {
        Image(
            bitmap = base64ToBitmap(imageData.imageBytes).asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(8.dp)
        )

        // Conditionally display the delete button
        if (imageData.userId == currentUser) {
            // Display the delete button
            Button(onClick = {
                // Todo
                Log.d("time of post", imageData.time.toString())
                lifecycleScope.launch {deletePost(imageData.time)}
            }) {
                Text("Delete")
            }
        }
    }


    // fixme: i am confused about how to configure serialization on the client side
    // my configure is a bit messy
    private suspend fun getResponseData(): List<ResponseData> {
        val response = KtorHttpClient.httpClient.get("http://10.0.2.2:8080/posts")
        val body = response.bodyAsText()
        return Json.decodeFromString(body)
    }

    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    // post time is unique, that's why del by time. Risk:Same time, multi pics shared.
    private suspend fun deletePost(time: Long): HttpResponse {
        Log.e("delete me", time.toString())
        return KtorHttpClient.httpClient.delete("http://10.0.2.2:8080/posts/$time")
    }




}