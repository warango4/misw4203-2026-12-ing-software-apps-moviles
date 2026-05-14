package com.misw.vinilos.utils

import android.view.View
import androidx.test.espresso.IdlingResource

class ViewVisibilityIdlingResource(
    private val view: View,
    private val expectedVisibility: Int
) : IdlingResource {

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = "ViewVisibilityIdlingResource"

    override fun isIdleNow(): Boolean {
        val idle = view.visibility == expectedVisibility
        if (idle) resourceCallback?.onTransitionToIdle()
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        resourceCallback = callback
    }
}
