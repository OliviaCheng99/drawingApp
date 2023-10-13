package com.example.customviewdemo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.io.FileOutputStream

class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Bitmap on which the drawing will be made
    private lateinit var bitmap: Bitmap

    // Canvas for the above bitmap
    private lateinit var bitmapCanvas: Canvas

    // Paint object for drawing
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    // To store points if the view is not yet initialized
    private var pendingPoints: List<MyPoint>? = null

    init {
        // Called when the view is constructed and has finished its layout
        post {
            pendingPoints?.let {
                drawPoints(it)
                pendingPoints = null
            }
        }
    }

    // Ensures that the bitmap is initialized with the correct width and height
    private fun ensureBitmap() {
        if (!::bitmap.isInitialized || bitmap.width != width || bitmap.height != height) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmapCanvas = Canvas(bitmap)
        }
    }

    // When the size of the custom view changes, reinitialize the bitmap
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        ensureBitmap()
    }

    // Draw the bitmap on the canvas of the custom view
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(bitmap, 0f, 0f, null)
    }

    // Draw points on the bitmap canvas based on shape, size, and color
    fun drawPoints(points: List<MyPoint>) {
        if (width == 0 || height == 0) {
            pendingPoints = points
            return
        }

        for (myPoint in points) {
            paint.color = myPoint.color
            paint.strokeWidth = myPoint.size

            when (myPoint.shape) {
                "Circle" -> bitmapCanvas.drawCircle(myPoint.point.x, myPoint.point.y, paint.strokeWidth, paint)
                "Rectangle" -> {
                    val halfWidth = paint.strokeWidth
                    bitmapCanvas.drawRect(myPoint.point.x - halfWidth, myPoint.point.y - halfWidth,
                        myPoint.point.x + halfWidth, myPoint.point.y + halfWidth, paint)
                }
                "Oval" -> {
                    val rect = RectF(myPoint.point.x - 75f, myPoint.point.y - 50f,
                        myPoint.point.x + 75f, myPoint.point.y + 50f)
                    bitmapCanvas.drawOval(rect, paint)
                }
            }
        }
        // Call onDraw to update the view
        invalidate()
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

    private fun clearDrawing() {
        if (::bitmap.isInitialized) {
            bitmap.eraseColor(Color.TRANSPARENT) // Clear the bitmap content
        }
        if (::bitmapCanvas.isInitialized) {
            bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }

        invalidate()
    }
}
