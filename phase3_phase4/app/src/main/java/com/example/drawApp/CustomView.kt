package com.example.drawApp

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Bitmap on which the drawing will be made
    private lateinit var bitmap: Bitmap

    // Canvas for the above bitmap
    private lateinit var bitmapCanvas: Canvas

    // Paint object for drawing
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    init {
        post {
            ensureBitmap()
        }
    }


    // Ensures that the bitmap is initialized with the correct width and height
    private fun ensureBitmap() {
        if (width > 0 && height > 0) {
            if (!::bitmap.isInitialized || bitmap.width != width || bitmap.height != height) {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                bitmapCanvas = Canvas(bitmap)
                bitmapCanvas.drawColor(Color.WHITE)
            }
        }
    }

    // When the size of the custom view changes, reinitialize the bitmap
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        ensureBitmap()
    }

    // Draw the bitmap on the canvas of the custom view
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }


    fun drawPaths(paths: MutableList<DrawingPath>) {
        // Check if bitmapCanvas is initialized before using it
        if (::bitmapCanvas.isInitialized) {
            paths.forEach {
                paint.color = it.color
                paint.strokeWidth = it.strokeWidth
                Log.d(
                    "in draw path method",
                    "${paint.color} ${paint.strokeWidth} ${it.path.toString()}"
                )
                bitmapCanvas.drawPath(it.path, paint)
            }

            // Call onDraw to update the view
            invalidate()
        } else {
            Log.e("CustomView", "bitmapCanvas has not been initialized.")
        }
    }

    // Returns the current drawing as a Bitmap
    fun getBitmap(): Bitmap {
        return bitmap.copy(Bitmap.Config.ARGB_8888, true)
    }

    // Save the current drawing (Bitmap) to internal storage
    fun saveDrawingToInternalStorage(filename: String): String? {
        try {
            // Open an output stream to write the bitmap data
            val fos = context.openFileOutput(filename, Context.MODE_PRIVATE)

            // Compress and write the bitmap data to the output stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)

            // Close the output stream
            fos.close()

            // Return the path of the saved drawing
            return context.getFileStreamPath(filename).absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun clearDrawing() {
        if (::bitmap.isInitialized) {
            bitmap.eraseColor(Color.WHITE) // Clear the bitmap content
        }
        if (::bitmapCanvas.isInitialized) {
            bitmapCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
        }
        invalidate()
    }


    suspend fun saveBitmapToGallery(context: Context) {
        withContext(Dispatchers.IO) {

            val timestamp = System.currentTimeMillis()

            //Tell the media scanner about the new file so that it is immediately available to the user.
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                values.put(MediaStore.Images.Media.IS_PENDING, true)
                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                if (uri != null) {
                    try {
                        val outputStream = context.contentResolver.openOutputStream(uri)
                        if (outputStream != null) {
                            try {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                outputStream.close()
                            } catch (e: Exception) {
                                Log.e(TAG, "saveBitmapImage: ", e)
                            }
                        }
                        values.put(MediaStore.Images.Media.IS_PENDING, false)
                        context.contentResolver.update(uri, values, null, null)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "saveBitmapImage: ", e)
                    }
                }
            }
        }
    }

    suspend fun shareBitmap(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                // Create a cache file to hold the bitmap data
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()
                val stream = FileOutputStream("$cachePath/image.png")
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                // Create a content URI from the file
                val imagePath = File(context.cacheDir, "images")
                val newFile = File(imagePath, "image.png")
                val contentUri =
                    FileProvider.getUriForFile(context, "com.example.drawApp.fileprovider", newFile)

                // Share the content URI using an Intent
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    type = "image/png"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun setImageBitmap(selectedImageBitmap: Bitmap?) {
        selectedImageBitmap?.let {
            clearDrawing() // Clear the current drawing

            val scaledBitmap = Bitmap.createScaledBitmap(it, width, height, true)
            bitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true)
            bitmapCanvas = Canvas(bitmap)
            invalidate() // To refresh the view

        }
    }

}

