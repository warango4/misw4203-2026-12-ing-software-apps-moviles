package com.misw.vinilos.albums

import android.widget.DatePicker
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import com.misw.vinilos.testutils.EspressoIdlingRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CreateAlbumE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val idlingRule = EspressoIdlingRule()

    @Before
    fun setCollectorRoleAndNavigate() {
        // Anclar en AlbumListFragment independientemente de dónde venga el runner
        onView(withId(R.id.AlbumListFragment)).perform(click())
        // Esperar que la lista cargue (idle = red inactiva)
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
        // Cambiar al rol Coleccionista mediante la UI: abre el chip y selecciona la opción.
        // Esto garantiza que updateRoleChip() y updateFab() sean invocados, y que la guardia
        // en CreateAlbumFragment.onViewCreated no haga popBackStack.
        onView(withId(R.id.action_role)).perform(click())
        onView(withId(R.id.optionCollector)).inRoot(isPlatformPopup()).perform(click())
        // Navegar directamente via NavController. El FAB está anclado con anchorGravity="top|end"
        // sobre el BottomNavigationView, por lo que su centro (y≈2190) queda exactamente en
        // el borde superior del BottomNav: el click de Espresso aterriza en el tab Collectors.
        activityRule.scenario.onActivity { activity ->
            Navigation.findNavController(activity, R.id.nav_host_fragment_content_main)
                .navigate(R.id.action_AlbumListFragment_to_CreateAlbumFragment)
        }
    }

    // E2E-HU07-01: El formulario muestra campos de nombre, portada, fecha, sello, género y descripción
    @Test
    fun e2e_hu07_01_formularioCrearAlbum_muestraTodosLosCampos() {
        onView(withId(R.id.etAlbumName))
            .check(matches(isDisplayed()))
        onView(withId(R.id.etAlbumCover))
            .perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.etAlbumReleaseDate))
            .perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.actvAlbumGenre))
            .perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.etAlbumDescription))
            .perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.actvAlbumRecordLabel))
            .perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.btnSaveAlbum))
            .perform(scrollTo()).check(matches(isDisplayed()))
    }

    // E2E-HU07-02: Completar todos los campos y pulsar Guardar navega de vuelta al catálogo
    @Test
    fun e2e_hu07_02_crearAlbumConDatosValidos_navegaDeVueltaAlCatalogo() {
        onView(withId(R.id.etAlbumName))
            .perform(typeText("Abbey Road E2E"), closeSoftKeyboard())

        onView(withId(R.id.etAlbumCover))
            .perform(scrollTo(), typeText("https://example.com/abbey_road.jpg"), closeSoftKeyboard())

        onView(withId(R.id.etAlbumReleaseDate))
            .perform(scrollTo(), click())
        onView(withClassName(equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(2024, 1, 15))
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.actvAlbumGenre))
            .perform(scrollTo(), click())
        onView(withText("Rock")).inRoot(isPlatformPopup()).perform(click())

        onView(withId(R.id.etAlbumDescription))
            .perform(scrollTo(), replaceText("E2E test album description"), closeSoftKeyboard())

        onView(withId(R.id.actvAlbumRecordLabel))
            .perform(scrollTo(), click())
        onView(withText("Sony Music")).inRoot(isPlatformPopup()).perform(click())

        onView(withId(R.id.btnSaveAlbum)).perform(scrollTo(), click())

        // Tras éxito, el fragmento regresa al catálogo de álbumes
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }

    // E2E-HU07-03: Dejar campos vacíos y pulsar Guardar muestra errores en los campos obligatorios
    @Test
    fun e2e_hu07_03_guardarConCamposVacios_muestraErroresYNoNavega() {
        onView(withId(R.id.btnSaveAlbum)).perform(scrollTo(), click())

        // El formulario sigue visible (no hubo navegación)
        onView(withId(R.id.btnSaveAlbum)).check(matches(isDisplayed()))

        // Campo nombre muestra error
        onView(withId(R.id.tilAlbumName))
            .check(matches(hasDescendant(allOf(withText("Campo obligatorio"), isDisplayed()))))

        // Campo portada muestra error
        onView(withId(R.id.tilAlbumCover))
            .check(matches(hasDescendant(allOf(withText("Campo obligatorio"), isDisplayed()))))
    }
}
