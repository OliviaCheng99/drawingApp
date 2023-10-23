
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawApp.DrawFragment
import com.example.drawApp.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawFragmentTest {

    private lateinit var scenario: FragmentScenario<DrawFragment>

    @Before
    fun setUp() {
        scenario = FragmentScenario.launchInContainer(DrawFragment::class.java)
    }

    @Test
    fun testFragmentInitialization() {
        // Perform assertions on the initial state of the fragment
        // For example, check if the Save Drawing button is displayed
        scenario.onFragment { fragment ->
            // Use runOnUiThread to perform UI testing
            fragment.activity?.runOnUiThread {
                onView(withId(R.id.saveDrawingButton)).check(matches(isDisplayed()))
            }
        }
    }

    // Add more test methods to cover different parts of your fragment
}
