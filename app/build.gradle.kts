plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ti.tecnoimport"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ti.tecnoimport"
        minSdk = 26 // Android 8.0+ según RNF6 del documento técnico
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase BoM y servicios (Auth, Firestore, Storage)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Autenticación Biométrica (Huella dactilar)
    implementation("androidx.biometric:biometric:1.1.0")

    // Geolocalización (Google Play Services Location)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Carga de imágenes desde Firebase Storage
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Almacenamiento seguro de credenciales (Ley 1581 de 2012)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}