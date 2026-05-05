import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.misw.vinilos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.misw.vinilos"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val baseUrl = project.findProperty("BASE_URL") as String? ?: "https://back-vynils-heroku.herokuapp.com/"
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.all {
            it.extensions.configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
        }
    }
}

tasks.register<JacocoReport>("jacocoUnitTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(
        fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
            include(
                "**/data/repository/*.class",
                "**/ui/albums/AlbumViewModel.class",
                "**/ui/albums/AlbumViewModel\$*.class",
                "**/ui/albums/AlbumViewModelFactory.class",
                "**/ui/albums/AlbumViewModelFactory\$*.class",
                "**/ui/albumdetail/AlbumDetailViewModel.class",
                "**/ui/albumdetail/AlbumDetailViewModel\$*.class",
                "**/ui/performers/PerformerViewModel.class",
                "**/ui/performers/PerformerViewModel\$*.class",
                "**/ui/performers/PerformerDetailViewModel.class",
                "**/ui/performers/PerformerDetailViewModel\$*.class",
                "**/ui/collectors/CollectorsViewModel.class",
                "**/ui/collectors/CollectorsViewModel\$*.class",
                "**/ui/collectors/CollectorDetailViewModel.class",
                "**/ui/collectors/CollectorDetailViewModel\$*.class",
            )
        }
    )

    sourceDirectories.setFrom(files("${projectDir}/src/main/java"))

    executionData.setFrom(
        fileTree(layout.buildDirectory.get()) {
            include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        }
    )
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.robolectric:robolectric:4.13")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    // Retrofit - llamadas HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp - caché HTTP y utilidades
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Glide - cargar imágenes desde URL
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
