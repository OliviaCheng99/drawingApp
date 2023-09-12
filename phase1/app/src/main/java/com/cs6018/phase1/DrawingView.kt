package com.cs6018.phase1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var currentPath: DrawPath? = null
    private var paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    fun setColor(color: Int) {
        paint.color = color
    }

    fun setStrokeWidth(width: Float) {
        paint.strokeWidth = width
    }

    fun getColor(): Int = paint.color

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = DrawPath(
                    path = Path().apply { moveTo(event.x, event.y) },
                    color = paint.color,
                    strokeWidth = paint.strokeWidth
                )
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath?.path?.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                currentPath?.let {
                    (context as MainActivity).viewModel.addPath(it)
                }
                currentPath = null
            }
        }

        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        (context as MainActivity).viewModel.paths.value?.forEach {
            paint.color = it.color
            paint.strokeWidth = it.strokeWidth
            canvas?.drawPath(it.path, paint)
        }
    }
}
