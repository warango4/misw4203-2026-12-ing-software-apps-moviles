package com.misw.vinilos.integration

import com.misw.vinilos.data.models.AlbumRequest
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.AlbumRepository
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlbumIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: VinilosApiService
    private lateinit var repository: AlbumRepository

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VinilosApiService::class.java)
        repository = AlbumRepository(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // El repositorio parsea y retorna correctamente la lista de álbumes desde la API
    @Test
    fun repositorioRetornaListaDeAlbumesDesdeLaApi() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """[
                    {"id":1,"name":"A Love Supreme","cover":"https://img.jpg","genre":"Jazz"},
                    {"id":2,"name":"Kind of Blue","cover":"https://img2.jpg","genre":"Jazz"}
                ]"""
            )
        )

        val result = repository.getAlbums()

        assertEquals(2, result.size)
        assertEquals("A Love Supreme", result[0].name)
        assertEquals("Kind of Blue", result[1].name)
        assertEquals(1, result[0].id)
        assertEquals("Jazz", result[0].genre)
    }

    // El repositorio parsea correctamente todos los campos opcionales del álbum
    @Test
    fun repositorioParseaTodosLosCamposDelAlbum() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """[{"id":10,"name":"Abbey Road","cover":"https://cover.jpg","genre":"Rock",
                   "description":"Iconic album","recordLabel":"Apple Records",
                   "releaseDate":"1969-09-26T00:00:00.000Z"}]"""
            )
        )

        val result = repository.getAlbums()

        assertEquals(1, result.size)
        val album = result[0]
        assertEquals(10, album.id)
        assertEquals("Abbey Road", album.name)
        assertEquals("Rock", album.genre)
        assertEquals("Iconic album", album.description)
        assertEquals("Apple Records", album.recordLabel)
        assertEquals("1969-09-26T00:00:00.000Z", album.releaseDate)
    }

    // El repositorio maneja correctamente una lista vacía desde la API
    @Test
    fun repositorioRetornaListaVaciaCuandoApiRespondeLista0Elementos() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("[]"))

        val result = repository.getAlbums()

        assertEquals(0, result.size)
    }

    // Al recibir HTTP 500, el repositorio lanza excepción (el ViewModel la convierte en error)
    @Test
    fun errorHttp500_repositorioLanzaExcepcion() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        var exceptionThrown = false
        try {
            repository.getAlbums()
        } catch (e: Exception) {
            exceptionThrown = true
        }

        assertTrue("Se esperaba excepción al recibir HTTP 500", exceptionThrown)
    }

    // Al recibir HTTP 404, el repositorio lanza excepción
    @Test
    fun errorHttp404_repositorioLanzaExcepcion() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        var exceptionThrown = false
        try {
            repository.getAlbum(999)
        } catch (e: Exception) {
            exceptionThrown = true
        }

        assertTrue("Se esperaba excepción al recibir HTTP 404", exceptionThrown)
    }

    // El repositorio retorna correctamente los tracks del álbum desde la API
    @Test
    fun repositorioRetornaTracksDelAlbumCorrectamente() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"id":5,"name":"Thriller","cover":"https://cover.jpg","genre":"Pop",
                   "tracks":[
                       {"id":1,"name":"Wanna Be Startin Somethin","duration":"6:02"},
                       {"id":2,"name":"Thriller","duration":"5:57"},
                       {"id":3,"name":"Billie Jean","duration":"4:54"}
                   ]}"""
            )
        )

        val album = repository.getAlbum(5)

        assertEquals("Thriller", album.name)
        assertEquals(3, album.tracks?.size)
        assertEquals("Wanna Be Startin Somethin", album.tracks?.get(0)?.name)
        assertEquals("6:02", album.tracks?.get(0)?.duration)
        assertEquals("Billie Jean", album.tracks?.get(2)?.name)
    }

    // El repositorio retorna álbum con performers asociados correctamente
    @Test
    fun repositorioRetornaAlbumConPerformersAsociados() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"id":3,"name":"Kind of Blue","cover":"url","genre":"Jazz",
                   "performers":[
                       {"id":1,"name":"Miles Davis","image":"url","description":"Jazz legend"}
                   ]}"""
            )
        )

        val album = repository.getAlbum(3)

        assertEquals("Kind of Blue", album.name)
        assertEquals(1, album.performers?.size)
        assertEquals("Miles Davis", album.performers?.get(0)?.name)
    }

    // El repositorio retorna álbum con álbum sin tracks (lista vacía o null)
    @Test
    fun INT04c_repositorioRetornaAlbumSinTracks() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"id":7,"name":"Single Track Album","cover":"url","genre":"Pop"}"""
            )
        )

        val album = repository.getAlbum(7)

        assertEquals("Single Track Album", album.name)
        assertTrue(album.tracks.isNullOrEmpty())
    }

    // El repositorio retorna el álbum creado con los datos del servidor
    @Test
    fun createAlbum_repositorioRetornaElAlbumCreado() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(201).setBody(
                """{"id":10,"name":"Abbey Road","cover":"https://cover.jpg","genre":"Rock",
                   "description":"Classic album","recordLabel":"Apple Records",
                   "releaseDate":"1969-09-26T00:00:00.000Z"}"""
            )
        )
        val request = AlbumRequest("Abbey Road", "https://cover.jpg", "1969-09-26T00:00:00.000Z", "Classic album", "Rock", "Apple Records")

        val result = repository.createAlbum(request)

        assertEquals(10, result.id)
        assertEquals("Abbey Road", result.name)
        assertEquals("Rock", result.genre)
    }

    // El repositorio parsea correctamente todos los campos del álbum creado
    @Test
    fun createAlbum_repositorioParseaTodosLosCamposDelAlbumCreado() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(201).setBody(
                """{"id":15,"name":"Kind of Blue","cover":"https://cover.jpg","genre":"Jazz",
                   "description":"Jazz masterpiece","recordLabel":"Columbia",
                   "releaseDate":"1959-08-17T00:00:00.000Z"}"""
            )
        )
        val request = AlbumRequest("Kind of Blue", "https://cover.jpg", "1959-08-17T00:00:00.000Z", "Jazz masterpiece", "Jazz", "Columbia")

        val result = repository.createAlbum(request)

        assertEquals(15, result.id)
        assertEquals("Kind of Blue", result.name)
        assertEquals("Jazz", result.genre)
        assertEquals("Jazz masterpiece", result.description)
        assertEquals("Columbia", result.recordLabel)
        assertEquals("1959-08-17T00:00:00.000Z", result.releaseDate)
    }

    // Al recibir HTTP 500 en createAlbum, el repositorio lanza excepción
    @Test
    fun createAlbum_errorHttp500_repositorioLanzaExcepcion() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val request = AlbumRequest("Abbey Road", "https://cover.jpg", "1969-09-26T00:00:00.000Z", "Classic album", "Rock", "Apple Records")

        var exceptionThrown = false
        try {
            repository.createAlbum(request)
        } catch (e: Exception) {
            exceptionThrown = true
        }

        assertTrue("Se esperaba excepción al recibir HTTP 500", exceptionThrown)
    }

    // El repositorio envía los datos correctos al servidor en el body de la petición
    @Test
    fun createAlbum_enviaLosDatosCorrectosAlServidor() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(201).setBody(
                """{"id":20,"name":"Thriller","cover":"https://cover.jpg","genre":"Pop"}"""
            )
        )
        val request = AlbumRequest("Thriller", "https://cover.jpg", "1982-11-30T00:00:00.000Z", "Pop masterpiece", "Pop", "Epic Records")

        repository.createAlbum(request)

        val recorded = mockWebServer.takeRequest()
        assertEquals("POST", recorded.method)
        assertTrue(recorded.path!!.endsWith("/albums"))
        val body = recorded.body.readUtf8()
        assertTrue(body.contains("\"name\":\"Thriller\""))
        assertTrue(body.contains("\"genre\":\"Pop\""))
        assertTrue(body.contains("\"recordLabel\":\"Epic Records\""))
    }
}
