package com.example.drawApp

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DrawingRepoTest {

    private lateinit var drawingRepo: DrawingRepo

    // create a dispatcher and coroutine scope
    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    // mock a drawdao
    @Mock
    private lateinit var mockDrawDao: DrawDao

    @Before
    fun setup() {
        // init Mockito anotation
        MockitoAnnotations.openMocks(this)

        // Mock the return value of getAllDrawings()
        Mockito.`when`(mockDrawDao.getAllDrawings()).thenReturn(flowOf(emptyList()))

        //create repo instance，pass mock stuff
        drawingRepo = DrawingRepo(testCoroutineScope, mockDrawDao)
    }

    @Test
    fun testAddDrawingToDB() {
        // mock data
        val filename = "TestFile"
        val filepath = "TestPath"

        // Mockito to mock  dao.addDrawData method
        testCoroutineScope.runBlockingTest {
            drawingRepo.addDrawingToDB(filename, filepath)

            // veryfiy dao.addDrawData is called
            Mockito.verify(mockDrawDao).addDrawData(
                DrawData(timestamp = any()
                    , filename = filename, filepath = filepath)
            )
        }
    }
}
