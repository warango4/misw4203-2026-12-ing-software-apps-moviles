package com.misw.vinilos.ui.albums

import android.view.ContextThemeWrapper
import android.widget.FrameLayout
import com.misw.vinilos.R
import com.misw.vinilos.data.models.Album
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlbumAdapterTest {

    @Test
    fun getItemCount_retornaLaCantidadDeAlbumes() {
        val albums = listOf(
            Album(1, "Album 1", "", "Rock"),
            Album(2, "Album 2", "", "Pop")
        )
        val adapter = AlbumAdapter { }
        adapter.submitList(albums)

        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun onBindViewHolder_muestraNombreGeneroYEjecutaClick() {
        val album = Album(1, "Revolver", "", "Rock")
        var clickedAlbum: Album? = null
        val adapter = AlbumAdapter { clickedAlbum = it }
        adapter.submitList(listOf(album))
        val themedContext = ContextThemeWrapper(RuntimeEnvironment.getApplication(), R.style.Theme_Vinilos)
        val parent = FrameLayout(themedContext)

        val holder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(holder, 0)

        assertEquals("Revolver", holder.binding.tvAlbumName.text.toString())
        assertEquals("Rock", holder.binding.tvAlbumGenre.text.toString())

        holder.binding.root.performClick()
        assertEquals(album, clickedAlbum)
    }
}
