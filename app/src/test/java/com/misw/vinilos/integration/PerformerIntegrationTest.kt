package com.misw.vinilos.integration

import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.PerformerRepository
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
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
class PerformerIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: VinilosApiService
    private lateinit var repository: PerformerRepository

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VinilosApiService::class.java)
        repository = PerformerRepository(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // Dispatcher enruta por path para manejar llamadas concurrentes a /musicians y /bands
    private fun setPathDispatcher(musiciansJson: String, bandsJson: String) {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when {
                    request.path?.contains("/musicians") == true ->
                        MockResponse().setResponseCode(200).setBody(musiciansJson)
                    request.path?.contains("/bands") == true ->
                        MockResponse().setResponseCode(200).setBody(bandsJson)
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
    }

    // El repositorio combina músicos y bandas en una única lista ordenada alfabéticamente
    @Test
    fun repositorioCombinaMusicosYBandasEnListaOrdenadaAlfabeticamente() = runTest {
        setPathDispatcher(
            musiciansJson = """[{"id":1,"name":"Zendaya","image":"url1","description":"Pop singer"}]""",
            bandsJson = """[{"id":2,"name":"Aventura","image":"url2","description":"Bachata band"}]"""
        )

        val result = repository.getPerformers()

        assertEquals(2, result.size)
        assertEquals("Aventura", result[0].name)  // orden alfabético
        assertEquals("Zendaya", result[1].name)
    }

    // Lista combinada con múltiples elementos de ambos tipos queda ordenada correctamente
    @Test
    fun listaCombinadaConMultiplesElementosOrdenadaCorrectamente() = runTest {
        setPathDispatcher(
            musiciansJson = """[
                {"id":1,"name":"Shakira","image":"url","description":"Colombian pop"},
                {"id":2,"name":"Carlos Vives","image":"url","description":"Vallenato"}
            ]""",
            bandsJson = """[
                {"id":3,"name":"The Beatles","image":"url","description":"Rock band"},
                {"id":4,"name":"Aterciopelados","image":"url","description":"Colombian rock"}
            ]"""
        )

        val result = repository.getPerformers()

        assertEquals(4, result.size)
        assertEquals("Aterciopelados", result[0].name)
        assertEquals("Carlos Vives", result[1].name)
        assertEquals("Shakira", result[2].name)
        assertEquals("The Beatles", result[3].name)
    }

    // Solo músicos con endpoint de bandas vacío retorna lista ordenada de músicos
    @Test
    fun soloMusicosConBandasVacias_retornaListaDeMusicosOrdenada() = runTest {
        setPathDispatcher(
            musiciansJson = """[
                {"id":1,"name":"Ruben Blades","image":"url","description":"Salsa"},
                {"id":2,"name":"Celia Cruz","image":"url","description":"Salsa queen"}
            ]""",
            bandsJson = "[]"
        )

        val result = repository.getPerformers()

        assertEquals(2, result.size)
        assertEquals("Celia Cruz", result[0].name)
        assertEquals("Ruben Blades", result[1].name)
    }

    // Solo bandas con endpoint de músicos vacío retorna lista ordenada de bandas
    @Test
    fun soloBandasConMusicosVacios_retornaListaDeBandasOrdenada() = runTest {
        setPathDispatcher(
            musiciansJson = "[]",
            bandsJson = """[
                {"id":1,"name":"Soda Stereo","image":"url","description":"Rock"},
                {"id":2,"name":"Los Prisioneros","image":"url","description":"Pop rock"}
            ]"""
        )

        val result = repository.getPerformers()

        assertEquals(2, result.size)
        assertEquals("Los Prisioneros", result[0].name)
        assertEquals("Soda Stereo", result[1].name)
    }

    // Ambos endpoints vacíos retorna lista vacía sin lanzar excepción
    @Test
    fun ambosEndpointsVacios_retornaListaVaciaSinExcepcion() = runTest {
        setPathDispatcher(musiciansJson = "[]", bandsJson = "[]")

        val result = repository.getPerformers()

        assertEquals(0, result.size)
    }

    // Al recibir HTTP 500 en /musicians, el repositorio lanza excepción
    @Test
    fun errorHttp500EnMusicos_repositorioLanzaExcepcion() = runTest {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(500)
            }
        }

        var exceptionThrown = false
        try {
            repository.getPerformers()
        } catch (e: Exception) {
            exceptionThrown = true
        }

        assertTrue("Se esperaba excepción al recibir HTTP 500", exceptionThrown)
    }

    // El repositorio retorna datos de músico con álbumes desde HTTP
    @Test
    fun repositorioRetornaDatosDeMusicoConAlbumes() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"id":10,"name":"Miles Davis","image":"url","description":"Jazz legend",
                   "birthDate":"1926-05-26T00:00:00.000Z",
                   "albums":[{"id":1,"name":"Kind of Blue","cover":"url","genre":"Jazz"}]}"""
            )
        )

        val performer = repository.getPerformer(10, isBand = false)

        assertEquals("Miles Davis", performer.name)
        assertEquals("Jazz legend", performer.description)
        assertEquals(1, performer.albums?.size)
        assertEquals("Kind of Blue", performer.albums?.get(0)?.name)
    }

    // El repositorio retorna datos de banda con múltiples álbumes desde HTTP
    @Test
    fun INT03_repositorioRetornaDatosDeBandaConMultiplesAlbumes() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"id":20,"name":"The Beatles","image":"url","description":"Legendary band",
                   "creationDate":"1960-01-01T00:00:00.000Z",
                   "albums":[
                       {"id":5,"name":"Abbey Road","cover":"url","genre":"Rock"},
                       {"id":6,"name":"Let It Be","cover":"url","genre":"Rock"}
                   ]}"""
            )
        )

        val performer = repository.getPerformer(20, isBand = true)

        assertEquals("The Beatles", performer.name)
        assertEquals(2, performer.albums?.size)
        assertEquals("Abbey Road", performer.albums?.get(0)?.name)
        assertEquals("Let It Be", performer.albums?.get(1)?.name)
    }
}
