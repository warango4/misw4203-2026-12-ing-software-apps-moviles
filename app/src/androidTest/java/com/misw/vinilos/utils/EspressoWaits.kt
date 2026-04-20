package com.misw.vinilos.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import java.util.concurrent.TimeoutException

/**
 * Utilidades de espera para estabilizar tests de Espresso en entornos lentos (CI).
 *
 * Evita el uso de Thread.sleep() esperando de forma activa hasta que se cumpla una condición.
 */
object EspressoWaits {

    /**
     * Espera hasta que el RecyclerView tenga al menos [minItemCount] items.
     */
    fun waitForRecyclerViewItemCount(
        minItemCount: Int,
        timeoutMs: Long = 30_000,
        intervalMs: Long = 250
    ): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = allOf(
                isDisplayed(),
                isAssignableFrom(RecyclerView::class.java)
            )

            override fun getDescription(): String =
                "Wait for RecyclerView to have at least $minItemCount items (timeout=$timeoutMs ms)"

            override fun perform(uiController: UiController, view: View) {
                val recyclerView = view as RecyclerView
                val startTime = System.currentTimeMillis()
                val endTime = startTime + timeoutMs

                do {
                    val itemCount = recyclerView.adapter?.itemCount ?: 0
                    if (itemCount >= minItemCount) return

                    uiController.loopMainThreadForAtLeast(intervalMs)
                } while (System.currentTimeMillis() < endTime)

                throw PerformException.Builder()
                    .withActionDescription(description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException("RecyclerView itemCount < $minItemCount after $timeoutMs ms"))
                    .build()
            }
        }
    }

    /**
     * Espera hasta que exista un view que cumpla [viewMatcher] en el árbol (útil para navegar entre pantallas).
     */
    fun waitForView(
        viewMatcher: Matcher<View>,
        timeoutMs: Long = 30_000,
        intervalMs: Long = 250
    ): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isDisplayed()

            override fun getDescription(): String =
                "Wait for a view matching: $viewMatcher (timeout=$timeoutMs ms)"

            override fun perform(uiController: UiController, view: View) {
                val startTime = System.currentTimeMillis()
                val endTime = startTime + timeoutMs

                do {
                    val matched = TreeIterables.breadthFirstViewTraversal(view)
                        .any { child -> viewMatcher.matches(child) }
                    if (matched) return

                    uiController.loopMainThreadForAtLeast(intervalMs)
                } while (System.currentTimeMillis() < endTime)

                throw PerformException.Builder()
                    .withActionDescription(description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException("View not found after $timeoutMs ms: $viewMatcher"))
                    .build()
            }
        }
    }
}


