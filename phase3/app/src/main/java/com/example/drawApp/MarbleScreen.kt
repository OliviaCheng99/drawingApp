package com.cs6018.marbleemulator

import android.content.Context
import android.graphics.Color
import android.graphics.RadialGradient
import android.graphics.Shader
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun MarbleScreen() {
    val marblePosition = remember { mutableStateOf(Offset(0f, 0f)) }

    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxW = this.maxWidth
        val maxH = this.maxHeight

        val circleDiameter = 50f
        val circleRadius = circleDiameter / 2

        val initialCenter = remember { mutableStateOf(true) }
        if (initialCenter.value) {
            marblePosition.value = Offset(maxW.value / 2 - circleRadius, maxH.value / 2 - circleRadius)
            initialCenter.value = false
        }

        SphereView(marblePosition.value.x.dp, marblePosition.value.y.dp)

        LaunchedEffect(Unit) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val x = event.values[0]
                    val y = event.values[1]

                    // Update the position based on sensor values
                    val deltaX = mapSensorValueToDelta(-x) // Note: x is negated here
                    val deltaY = mapSensorValueToDelta(y)

                    marblePosition.value = marblePosition.value.copy(
                        x = (marblePosition.value.x + deltaX).coerceIn(circleDiameter, maxW.value- circleDiameter),
                        y = (marblePosition.value.y + deltaY).coerceIn(circleDiameter, maxH.value- circleDiameter)
                    )
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                }
            }

            sensorManager.registerListener(listener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }
}

fun mapSensorValueToDelta(sensorValue: Float): Float {
    val multiplier = .5f // Adjust this for sensitivity
    return sensorValue * multiplier
}

@Composable
fun SphereView(offsetX: Dp, offsetY: Dp) {
    Canvas(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(50.dp)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        val colors = intArrayOf(Color.WHITE, Color.BLUE, Color.GRAY)
        val stops = floatArrayOf(0f, 0.5f, 1f)

        val radialGradient = RadialGradient(
            center.x, center.y, radius,
            colors, stops,
            Shader.TileMode.CLAMP
        )

        val brush = ShaderBrush(radialGradient)

        drawCircle(brush = brush, radius = radius)
    }
}