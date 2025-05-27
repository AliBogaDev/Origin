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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation(libs.gms.play.services.maps)
    implementation (libs.glide)
    annotationProcessor (libs.compiler.v4130)
    implementation(libs.gms.play.services.maps)
    implementation(libs.appcompat)
    implementation(libs.preference.ktx)
    implementation(libs.volley)
    implementation(libs.appcompat)
    implementation(libs.material.v1120)
    implementation(libs.constraintlayout.v221)
    androidTestImplementation(libs.espresso.core.v361)
    implementation(libs.constraintlayout.v204)
    implementation (libs.core)
    implementation (libs.zxing.android.embedded)
    implementation (libs.okhttp)
    implementation (libs.picasso)
    implementation (libs.gson)
    implementation (libs.glide)
    annotationProcessor (libs.compiler.v471)
    implementation (libs.json)
    implementation (libs.material.v140)
    implementation (libs.zxing.android.embedded.v430)
    implementation (libs.material.v1120)
    implementation (libs.zxing.android.embedded.v430)



}






