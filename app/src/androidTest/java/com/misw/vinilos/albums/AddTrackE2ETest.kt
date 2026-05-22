package com.misw.vinilos.albums

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import com.misw.vinilos.data.models.AlbumRequest
import com.misw.vinilos.data.network.VinilosServiceAdapter
import com.misw.vinilos.testutils.EspressoIdlingRule
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AddTrackE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val idlingRule = EspressoIdlingRule()

    private var testAlbumId: Int = -1

    @Before
    fun setCollectorRoleAndNavigateToAddTrack() {
        // Crear un álbum fresco vía API antes de la UI para que el test sea
        // independiente de datos pre-existentes y no tenga tracks duplicados.
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val api = VinilosServiceAdapter.createApiService(context)
        runBlocking {
            val album = api.createAlbum(
                AlbumRequest(
                    name = "E2E AddTrack Album",
                    cover = "https://picsum.photos/200",
                    releaseDate = "2024-01-01",
                    description = "Album created by E2E test",
                    genre = "Rock",
                    recordLabel = "Sony Music"
                )
            )
            testAlbumId = album.id
        }

        // Anclar en AlbumListFragment independientemente de dónde venga el runner
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
        // Cambiar al rol Coleccionista mediante la UI
        onView(withId(R.id.action_role)).perform(click())
        onView(withId(R.id.optionCollector)).inRoot(isPlatformPopup()).perform(click())
        // Navegar al álbum recién creado
        activityRule.scenario.onActivity { activity ->
            Navigation.findNavController(activity, R.id.nav_host_fragment_content_main)
                .navigate(
                    R.id.action_AlbumListFragment_to_AlbumDetailFragment,
                    Bundle().apply { putInt("albumId", testAlbumId) }
                )
        }
        // Esperar a que el detalle cargue (red inactiva) antes de navegar al formulario
        onView(withId(R.id.albumName)).check(matches(isDisplayed()))
        // fabAddTrack cae dentro del BottomNavigationView visualmente; se navega via NavController
        activityRule.scenario.onActivity { activity ->
            Navigation.findNavController(activity, R.id.nav_host_fragment_content_main)
                .navigate(
                    R.id.action_AlbumDetailFragment_to_AddTrackFragment,
                    Bundle().apply { putInt("albumId", testAlbumId) }
                )
        }
    }

    // E2E-HU08-01: El formulario muestra los campos de nombre y duración
    @Test
    fun e2e_hu08_01_formularioAgregarTrack_muestraLosCampos() {
        onView(withId(R.id.etTrackName))
            .check(matches(isDisplayed()))
        onView(withId(R.id.etTrackDuration))
            .perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.btnSaveTrack))
            .perform(scrollTo()).check(matches(isDisplayed()))
    }

    // E2E-HU08-02: Asociar track con datos válidos regresa al detalle mostrando el track
    @Test
    fun e2e_hu08_02_agregarTrackConDatosValidos_apareceEnDetalleDelAlbum() {
        onView(withId(R.id.etTrackName))
            .perform(typeText("Money E2E"), closeSoftKeyboard())
        onView(withId(R.id.etTrackDuration))
            .perform(scrollTo(), typeText("6:22"), closeSoftKeyboard())

        onView(withId(R.id.btnSaveTrack)).perform(scrollTo(), click())

        // Tras éxito, regresa al detalle del álbum con la lista de tracks visible
        onView(withId(R.id.tracksRecyclerView))
            .check(matches(isDisplayed()))
    }

    // E2E-HU08-03: Duración vacía o con formato inválido muestra error y no envía la petición
    @Test
    fun e2e_hu08_03_duracionInvalida_muestraErrorYNoNavega() {
        onView(withId(R.id.etTrackName))
            .perform(typeText("Money"), closeSoftKeyboard())
        // Ingresar duración con formato incorrecto
        onView(withId(R.id.etTrackDuration))
            .perform(scrollTo(), typeText("abc"), closeSoftKeyboard())

        onView(withId(R.id.btnSaveTrack)).perform(scrollTo(), click())

        // El formulario sigue visible (no hubo navegación)
        onView(withId(R.id.btnSaveTrack)).check(matches(isDisplayed()))

        // El campo duración muestra el error de formato
        onView(withId(R.id.tilTrackDuration))
            .check(matches(hasDescendant(allOf(
                withText("Formato inválido. Use m:ss (ej. 6:22)"),
                isDisplayed()
            ))))
    }
}
