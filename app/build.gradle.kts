plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.itq.zorronotas"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.itq.zorronotas"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // Jetpack Lifecycle & ViewModel / LiveData
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)

    // Room ORM Persistence
    implementation(libs.androidx.room.runtime)
    implementation(libs.core.splashscreen)
    annotationProcessor(libs.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}