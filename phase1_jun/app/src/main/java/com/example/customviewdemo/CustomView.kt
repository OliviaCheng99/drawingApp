package com.example.customviewdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
//
//class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {
//
//    private lateinit var bitmap: Bitmap
//    private lateinit var bitmapCanvas: Canvas
//    private val paint = Paint().apply {
//        color = Color.RED
//        style = Paint.Style.STROKE
//        strokeWidth = 10f
//    }
//
//    // Ensure that the bitmap size matches the view size
//    private fun ensureBitmap() {
//        if (!::bitmap.isInitialized || bitmap.width != width || bitmap.height != height) {
//            Log.e("height and width", "$height $width")
//            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//            bitmapCanvas = Canvas(bitmap)
//        }
//    }
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        ensureBitmap()
//    }
//
//    // override the onDraw method of View class
//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//        canvas?.drawBitmap(bitmap, 0f, 0f, null) // start from left top corner
//    }
//
//    fun drawPoints(points: List<MyPoint>) {
//        ensureBitmap()
//        for (myPoint in points) {
//            paint.color = myPoint.color
//            paint.strokeWidth = myPoint.size
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
//        invalidate()  // redraw
//    }
//
//
//    fun setCurrentPaintColor(color: Int) {
//        paint.color = color
//    }
//
//    fun setCurrentPaintSize(size: Float) {
//        paint.strokeWidth = size
//    }
//
//}


class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private lateinit var bitmap: Bitmap
    private lateinit var bitmapCanvas: Canvas
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private var pendingPoints: List<MyPoint>? = null

    // execute when custom view constructed
    init {
        post { // when the view is constructed and it has finished its layout, post bolck will be excecuted
            pendingPoints?.let {//not null and execute
                drawPoints(it)  // here the width and height are not 0
                pendingPoints = null
            }
        }
    }

    private fun ensureBitmap() {
        if (!::bitmap.isInitialized || bitmap.width != width || bitmap.height != height) {
//            Log.e("height and width", "$height $width")
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmapCanvas = Canvas(bitmap)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        ensureBitmap()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(bitmap, 0f, 0f, null)
    }

    fun drawPoints(points: List<MyPoint>) {
        if (width == 0 || height == 0) { // first check the if the view's width and height. If 0, draw nothing
            pendingPoints = points
            return
        }
//        ensureBitmap()

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
        invalidate() // notify that ondraw function should be called  to render the screen
    }

//    fun setCurrentPaintColor(color: Int) {
//        paint.color = color
//    }
//
//    fun setCurrentPaintSize(size: Float) {
//        paint.strokeWidth = size
//    }
}

