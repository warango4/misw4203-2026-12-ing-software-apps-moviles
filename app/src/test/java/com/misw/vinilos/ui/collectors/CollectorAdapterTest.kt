package com.misw.vinilos.ui.collectors

import android.widget.FrameLayout
import com.misw.vinilos.data.models.Collector
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CollectorAdapterTest {

    @Test
    fun getItemCount_retornaLaCantidadDeColeccionistas() {
        val collectors = listOf(
            Collector(1, "Coleccionista 1", "123", "a@a.com"),
            Collector(2, "Coleccionista 2", "456", "b@b.com")
        )
        val adapter = CollectorAdapter(collectors) { }
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun onBindViewHolder_muestraDatosYEjecutaClick() {
        val collector = Collector(1, "Manolo Bellon", "3502457896", "manollo@caracol.com.co")
        var clickedCollector: Collector? = null
        val adapter = CollectorAdapter(listOf(collector)) { clickedCollector = it }
        val context = androidx.appcompat.view.ContextThemeWrapper(RuntimeEnvironment.getApplication(), com.misw.vinilos.R.style.Theme_Vinilos)
        val parent = FrameLayout(context)
        val holder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(holder, 0)

        assertEquals("Manolo Bellon", holder.binding.tvCollectorName.text.toString())
        assertEquals("3502457896", holder.binding.tvCollectorPhone.text.toString())
        assertEquals("manollo@caracol.com.co", holder.binding.tvCollectorEmail.text.toString())

        holder.binding.root.performClick()
        assertEquals(collector, clickedCollector)
    }
}

