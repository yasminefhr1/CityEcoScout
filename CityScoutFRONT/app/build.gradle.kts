plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "ma.ensa.projet"
    compileSdk = 34

    defaultConfig {
        applicationId = "ma.ensa.projet"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    // Compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.foundation:foundation:1.5.1")
    implementation("androidx.compose.material:material:1.5.1")
    implementation("androidx.compose.material:material-icons-extended:1.5.1")
    implementation("androidx.compose.animation:animation:1.5.1")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Accompanist libraries
    implementation("com.google.accompanist:accompanist-swiperefresh:0.28.0")
    implementation("com.google.accompanist:accompanist-permissions:0.28.0")

    // Google Play Services and Maps
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:maps-compose:2.13.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
   // implementation ("com.google.android.gms:play-services-street-view:18.2.0")
    implementation("androidx.webkit:webkit:1.8.0")


    // Networking (Retrofit & Ktor)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("io.ktor:ktor-client-core:2.3.2")
    implementation("io.ktor:ktor-client-okhttp:2.3.2")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.2")
    implementation ("io.ktor:ktor-client-android:2.3.2")
    implementation ("io.ktor:ktor-client-json:2.3.2")
    implementation ("io.ktor:ktor-client-serialization:2.3.2")



    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.4.0")

    // WebView
    implementation("androidx.webkit:webkit:1.6.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.generativeai)

    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
}
