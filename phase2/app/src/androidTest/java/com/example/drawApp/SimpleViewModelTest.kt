package com.example.drawApp

import android.graphics.Path
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Observer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SimpleViewModelTest {

    //    Because of LiveData, we need this to inspect background data
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: SimpleViewModel

    @Mock
    private lateinit var drawingRepo: Repo

    @Mock
    private lateinit var pathsObserver: Observer<MutableList<DrawingPath>>


    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = SimpleViewModel(drawingRepo)
        viewModel.paths.observeForever(pathsObserver)
    }

    @Test
    fun `test update color`() {
        val newColor = Color.Red.toArgb()

        viewModel.updateColor(newColor)

        assertEquals(newColor, viewModel.selectedColor.value)
    }

    @Test
    fun `test update stroke`() {
        val newStroke = "20"

        viewModel.updateStroke(newStroke)

        assertEquals(newStroke, viewModel.strokeWidth.value)
    }

    @Test
    fun `test add path`() {
        val newPath = Path()
        val expectedSize = viewModel.paths.value?.size?.plus(1) ?: 1

        viewModel.addPath(newPath)

        assertEquals(expectedSize, viewModel.paths.value?.size)
    }
}
