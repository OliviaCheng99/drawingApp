package com.example.drawApp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.View

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
        post{
            ensureBitmap()
        }
    }


    // Ensures that the bitmap is initialized with the correct width and height
    private fun ensureBitmap() {
        if (width > 0 && height > 0) {
            if (!::bitmap.isInitialized || bitmap.width != width || bitmap.height != height) {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                bitmapCanvas = Canvas(bitmap)
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
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }


    fun drawPaths(paths: MutableList<DrawingPath>){
        // Check if bitmapCanvas is initialized before using it
        if (::bitmapCanvas.isInitialized) {
            paths.forEach {
                paint.color = it.color
                paint.strokeWidth = it.strokeWidth
                Log.d("in draw path method", "${paint.color} ${paint.strokeWidth} ${it.path.toString()}")
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

    private fun clearDrawing() {
        if (::bitmap.isInitialized) {
            bitmap.eraseColor(Color.TRANSPARENT) // Clear the bitmap content
        }
        if (::bitmapCanvas.isInitialized) {
            bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }

        invalidate()
    }


    // Draw points on the bitmap canvas based on shape, size, and color
//    fun drawPoints(points: List<MyPoint>) {
//        for (myPoint in points) {
//            paint.color = myPoint.color
//            paint.strokeWidth = myPoint.size
//            Log.e("shape!!!", myPoint.toString())
//
//            when (myPoint.shape) {
//                "Circle" -> bitmapCanvas.drawCircle(myPoint.point.x, myPoint.point.y, paint.strokeWidth, paint)
//                "Rectangle" -> {
//                    val halfWidth = paint.strokeWidth
//                    bitmapCanvas.drawRect(myPoint.point.x - halfWidth, myPoint.point.y - halfWidth,
//                        myPoint.point.x + halfWidth, myPoint.point.y + halfWidth, paint)
//                }
//                "Oval" -> {
//                    val rect = RectF(myPoint.point.x - 75f, myPoint.point.y - 50f,
//                        myPoint.point.x + 75f, myPoint.point.y + 50f)
//                    bitmapCanvas.drawOval(rect, paint)
//                }
//            }
//        }
//        // Call onDraw to update the view
//        invalidate()
//    }
}
