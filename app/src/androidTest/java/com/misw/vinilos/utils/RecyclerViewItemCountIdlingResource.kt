package com.misw.vinilos.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.IdlingResource

class RecyclerViewItemCountIdlingResource(
    private val recyclerView: RecyclerView,
    private val minItemCount: Int = 1
) : IdlingResource {

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = "RecyclerViewItemCountIdlingResource"

    override fun isIdleNow(): Boolean {
        val idle = (recyclerView.adapter?.itemCount ?: 0) >= minItemCount
        if (idle) resourceCallback?.onTransitionToIdle()
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        resourceCallback = callback
    }
}
