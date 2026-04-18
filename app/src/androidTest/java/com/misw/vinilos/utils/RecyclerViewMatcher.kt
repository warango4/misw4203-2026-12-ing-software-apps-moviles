package com.misw.vinilos.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher(private val recyclerViewId: Int) {

    /**
     * @param position índice del ítem en el adaptador (0 = primer ítem)
     * @param targetViewId ID de la vista dentro del ítem (ej: R.id.tvAlbumName)
     */
    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> =
        object : TypeSafeMatcher<View>() {
            private var childView: View? = null

            override fun describeTo(description: Description) {
                description.appendText(
                    "RecyclerView id=$recyclerViewId, posición=$position, vista id=$targetViewId"
                )
            }

            override fun matchesSafely(view: View): Boolean {
                if (childView == null) {
                    val recyclerView = view.rootView.findViewById<RecyclerView>(recyclerViewId)
                    childView = recyclerView
                        ?.findViewHolderForAdapterPosition(position)
                        ?.itemView
                }
                return childView?.let {
                    if (targetViewId == -1) view === it
                    else view === it.findViewById<View>(targetViewId)
                } ?: false
            }
        }
}
