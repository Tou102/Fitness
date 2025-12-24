import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    kotlin("kapt")
}

android {
    namespace = "com.example.fitness"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fitness"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Đọc file local.properties
        val localProps = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) file.inputStream().use { load(it) }
        }

        // Lấy GEMINI_API_KEY từ local.properties
        buildConfigField(
            type = "String",
            name = "GEMINI_API_KEY",
            value = "\"${localProps["GEMINI_API_KEY"]}\""
        )
    }


    buildTypes {
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
        compose = true
        viewBinding = true
        buildConfig = true

    }
}

dependencies {

    // ---------------------------------------
    // 🔥 OpenAI API (chat + phân tích ảnh)
    // ---------------------------------------
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.29")
    implementation(libs.androidx.games.activity)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.benchmark.traceprocessor.android)
// Room compiler – DÒNG NÀY BẮT BUỘC PHẢI CÓ!
    kapt("androidx.room:room-compiler:2.6.1")
    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation("androidx.recyclerview:recyclerview:1.4.0-alpha02")

    // MLKit Pose Detection
    implementation("com.google.mlkit:pose-detection:17.0.0")
    implementation("com.google.mlkit:pose-detection-accurate:17.0.0")
    implementation("com.google.mlkit:vision-common:17.3.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.android)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")



    // Icons
    implementation("androidx.compose.material:material-icons-extended")


    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-gif:2.7.0")

    // Google Maps
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.maps.android:maps-compose:2.13.0")

    // Accompanist
    implementation("com.google.accompanist:accompanist-pager:0.31.5-beta")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.31.5-beta")
    implementation("com.google.accompanist:accompanist-permissions:0.31.2-alpha")



    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
