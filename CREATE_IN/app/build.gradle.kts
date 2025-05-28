plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.example.createinn"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.createinn"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {



        // Core AndroidX
        implementation(libs.appcompat)
        implementation(libs.material) // Elige una sola versión (ej: v1120)
        implementation(libs.activity)
        implementation(libs.constraintlayout) // Elige la versión más reciente que uses

        // Network & JSON
        implementation(libs.volley)
        implementation(libs.okhttp)
        implementation(libs.gson)

        // Image loading
        implementation(libs.glide)
        annotationProcessor(libs.compiler) // Solo una vez
        implementation(libs.picasso) // Si realmente usas ambos (glide + picasso)

        // ZXing barcode
        implementation (libs.zxing.android.embedded.v430)
         implementation (libs.zxing)

    // Google services
        implementation(libs.gms.play.services.maps)

        // Preferences
        implementation(libs.preference.ktx)

        // Utils
        implementation(libs.core)
        implementation(libs.json)

        // Testing
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
    }












