package com.example.drawApp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class DataBaseTest {

    private lateinit var db: DrawingDatabase
    private lateinit var drawDao: DrawDao

    @Before
    fun setup() {
        // in-memory database
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DrawingDatabase::class.java
        ).build()

        drawDao = db.drawDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testAddDrawing() = runBlocking {
        val drawData = DrawData(Date(), "testFile", "testPath")
        drawDao.addDrawData(drawData)
        val retrievedData = drawDao.getDrawingByName("testFile")
        assertNotNull(retrievedData)
        assertEquals("testFile", retrievedData?.filename)
    }
}
