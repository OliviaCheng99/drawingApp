package com.example.drawApp

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DrawingApplication:Application() {
        val scope = CoroutineScope(SupervisorJob())
        val db by lazy {DrawingDatabase.getDatabase(applicationContext)}
        val drawingRepo by lazy {DrawingRepo(scope, db.drawDao())}
}