# Vinilos - Aplicacion Movil Android

Proyecto desarrollado para **The Software Design Company (TSDC)** como parte del curso de la maestría MISO de la Universidad de los Andes.

---

## Conformacion de equipo

**Nombre del equipo:** Los mejores

| Apellidos          | Nombres         | Email Uniandes                | Usuario GitHub  |
|--------------------|-----------------|-------------------------------|-----------------|
| Arango Chavarria   | Wendy Maria     | w.arangoc@uniandes.edu.co    | warango4        |
| Echeverry Bohorquez| Andres Felipe   | a.echeverryb@uniandes.edu.co | afecheverryb10  |
| Vega Guarin        | Juan Sebastian  | js.vega1@uniandes.edu.co     | jsebasvegag     |
| Urian Villamil     | Julio Cesar     | j.urianv@uniandes.edu.co     | jurianvilla     |

---

## 1. Stack Tecnológico en este proyecto
- **Lenguaje:** Kotlin
- **Arquitectura:** MVVM (Model-View-ViewModel) recomendada por Google.
- **Herramientas Nativas Android:** Android SDK, AndroidX, ViewBinding.
- **Asincronismo:** Kotlin Coroutines.
- **Navegación:** Jetpack Navigation Component.
- **Redes y API:** Retrofit 2 + Gson Converter.
- **Consumo de Imágenes:** Glide.
- **Pruebas Unitarias:** JUnit 4 + Robolectric + Arch Core Testing + Coroutines Test.

---

## 2. Cómo compilar

Este proyecto utiliza el sistema de construcción de **Gradle**. Para compilar la aplicación, puedes abrir una terminal en la raíz del proyecto y ejecutar los siguientes comandos:

**En Linux/macOS:**
```bash
# Otorgar permisos al wrapper si es necesario
chmod +x gradlew

# Limpiar compilaciones previas
./gradlew clean

# Compilar la aplicación en modo Debug y generar el APK
./gradlew assembleDebug
```

Para generar un Release y validar todo el proyecto estructuralmente:
```bash
./gradlew build
```

### Verificación rápida de compilación (tipo "clean + build")

Si quieres validar que **todo compila desde cero** (APK debug + unit tests + androidTest), ejecuta:

```bash
./gradlew clean assembleDebug assembleDebugUnitTest assembleDebugAndroidTest
```

---

## 3. Cómo ejecutar todos los tests

Contamos con una amplia cobertura de pruebas unitarias implementadas usando JUnit y Robolectric.

Para ejecutar todo el set de pruebas, procesar los resultados y verificar el correcto comportamiento de la lógica, ejecuta:

```bash
./gradlew test
```

### Comandos de verificación (orden recomendado)

Para validar el proyecto exactamente en el orden usado en CI/local, ejecuta:

```bash
./gradlew clean :app:testDebugUnitTest
./gradlew test
./gradlew :app:assembleDebugUnitTest :app:assembleDebugAndroidTest
./gradlew :app:jacocoUnitTestReport
```

### Unit tests (debug)

Para correr únicamente los unit tests del módulo `app` en Debug:

```bash
./gradlew :app:testDebugUnitTest
```

Los reportes HTML detallados de las pruebas se generarán internamente en:
`app/build/reports/tests/testDebugUnitTest/index.html`

### Coverage (JaCoCo) - Unit tests

El proyecto cuenta con un task de JaCoCo para generar el reporte de cobertura de **unit tests**:

```bash
./gradlew :app:jacocoUnitTestReport
```

El reporte HTML se genera en:

`app/build/reports/jacoco/jacocoUnitTestReport/html/index.html`

El XML (útil para CI/sonar) se genera en:

`app/build/reports/jacoco/jacocoUnitTestReport/jacocoUnitTestReport.xml`

### Tests de UI (Espresso)

Para compilar/ensamblar el APK de instrumentation tests (sin ejecutarlos):

```bash
./gradlew assembleDebugAndroidTest
```

Para **ejecutar** los tests de Espresso se requiere un emulador o dispositivo conectado:

```bash
./gradlew :app:connectedDebugAndroidTest
```

---

## 4. Ejemplos de cURL (Historias de Usuario)

Para verificar y reproducir las respuestas del servidor que utilizamos para construir las funcionalidades, a continuación exponemos ejemplos rápidos en Bash.

La aplicación consume el backend definido en `BuildConfig.BASE_URL`.

### HU01 - Consultar Catálogo de Álbumes
Muestra el listado de álbumes disponibles.
* **Path:** `GET /albums`

```bash
curl -X GET "https://back-vynils-heroku.herokuapp.com/albums" \
     -H "Accept: application/json"
```

**Ejemplo Response:**
```json
[
  {
    "id": 100,
    "name": "Buscando América",
    "cover": "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
    "releaseDate": "1984-08-01T00:00:00.000Z",
    "description": "Buscando América es el primer álbum de la banda de Rubén Blades...",
    "genre": "Salsa",
    "recordLabel": "Elektra"
  }
]
```

### HU02 - Consultar Información Detallada de un Álbum
Muestra el detalle del disco, incluyendo sus canciones y comentarios.
* **Path:** `GET /albums/{id}`

```bash
curl -X GET "https://back-vynils-heroku.herokuapp.com/albums/100" \
     -H "Accept: application/json"
```

**Ejemplo Response:**
```json
{
  "id": 100,
  "name": "Buscando América",
  "cover": "https://i.pinimg.com/564x/...",
  "releaseDate": "1984-08-01T00:00:00.000Z",
  "description": "...",
  "genre": "Salsa",
  "recordLabel": "Elektra",
  "tracks": [
    {
      "id": 101,
      "name": "Decisiones",
      "duration": "5:05"
    }
  ],
  "performers": [],
  "comments": []
}
```

### HU03 - Consultar Catálogo de Artistas
El usuario verá una lista de músicos y bandas unidos.
* **Paths:** `GET /musicians`  y  `GET /bands`

```bash
# Consultar músicos
curl -X GET "https://back-vynils-heroku.herokuapp.com/musicians" \
     -H "Accept: application/json"

# Consultar bandas
curl -X GET "https://back-vynils-heroku.herokuapp.com/bands" \
     -H "Accept: application/json"
```

**Ejemplo Response (Músicos):**
```json
[
  {
    "id": 100,
    "name": "Rubén Blades Bellido de Luna",
    "image": "https://upload.wikimedia.org/...",
    "description": "Es un cantante, compositor, músico, actor...",
    "birthDate": "1948-07-16T00:00:00.000Z"
  }
]
```

### HU04 - Consultar Información Detallada de un Artista
Detalle individual que trae la relación con sus álbumes y premios.
* **Paths:** `GET /musicians/{id}`  ó  `GET /bands/{id}`

### HU06 - Consultar la información detallada de un coleccionista
Como usuario visitante quiero ver el detalle de un coleccionista para conocer sus gustos musicales.
* **Path:** `GET /collectors/{id}`

```bash
curl -X GET "https://vinyls-backend-miso-01cdf4b5b598.herokuapp.com/collectors/1" \
     -H "Accept: application/json"
```

```bash
curl -X GET "https://back-vynils-heroku.herokuapp.com/bands/2" \
     -H "Accept: application/json"
```

**Ejemplo Response (Bandas):**
```json
{
  "id": 2,
  "name": "Queen",
  "image": "https://pm1.narvii.com/...",
  "description": "Queen es una banda británica de rock...",
  "creationDate": "1970-01-01T00:00:00.000Z",
  "albums": [
    {
      "id": 102,
      "name": "A Night at the Opera",
      "cover": "https://upload.wikimedia.org/...",
      "releaseDate": "1975-11-21T00:00:00.000Z",
      "description": "Es el cuarto álbum de estudio...",
      "genre": "Rock",
      "recordLabel": "EMI"
    }
  ],
  "musicians": [],
  "performerPrizes": []
}
```
