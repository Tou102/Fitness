plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

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
    }
}

dependencies {

    implementation(libs.androidx.runtime.livedata)
    implementation(libs.play.services.location)
    val room_version = "2.6.1"
    implementation ("com.google.accompanist:accompanist-pager:0.31.5-beta")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.31.5-beta")




    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation ("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-gif:2.5.0")
    implementation("com.airbnb.android:lottie-compose:6.0.0")
    implementation ("androidx.compose.material3:material3:1.0.1")
    implementation ("androidx.compose.material:material-icons-extended:<version>")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.gms:play-services-maps:18.0.2")
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.29")
    implementation ("com.google.maps.android:maps-compose:2.13.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")


    // CameraX
    implementation ("androidx.camera:camera-camera2:1.4.2")
    implementation ("androidx.camera:camera-lifecycle:1.4.2")
    implementation ("androidx.camera:camera-view:1.3.0")

// ML Kit Pose Detection (Beta)
    implementation ("com.google.mlkit:pose-detection:18.0.0-beta5")
    implementation ("com.google.mlkit:pose-detection-accurate:18.0.0-beta5")

    implementation ("com.google.accompanist:accompanist-permissions:0.31.2-alpha")


    // Room
    implementation ("androidx.room:room-runtime:2.7.1")


    kapt ("androidx.room:room-compiler:2.7.1")


}