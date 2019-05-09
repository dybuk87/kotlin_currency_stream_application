package pl.dybuk.currencytest.ui.exchange

import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.dybuk.currencytest.R
import pl.dybuk.currencytest.RecyclerViewMatcher


@RunWith(AndroidJUnit4::class)
class CurrencyActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<CurrencyActivity>
            = ActivityTestRule(CurrencyActivity::class.java)

    fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
        return RecyclerViewMatcher(recyclerViewId)
    }

    @Test
    fun changeText_sameActivity() {
    }

}