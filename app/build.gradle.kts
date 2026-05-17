plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.ksp.petcaretracker"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.ksp.petcaretracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false

            resValue("string", "app_name", "PetCare Debug")
            buildConfigField("String", "BUILD_ENV", "\"DEBUG\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "app_name", "PetCareTracker")
            buildConfigField("String", "BUILD_ENV", "\"RELEASE\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(project(":ksp-core-library"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    //Navigation Graph - Compose
    implementation(libs.androidx.navigation.compose)

    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    //Lottie
    implementation(libs.lottie.compose)

    //Room DB
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

//     Billing Library
    implementation(libs.billing)

    // Pdf into Images
    implementation(libs.androidx.compose.foundation)

    //WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Biometric Authentication
    implementation(libs.androidx.biometric)

    // Material Icons for Compose
    implementation(libs.androidx.compose.material.icons.extended)

    // Compose Animation
    implementation("androidx.compose.animation:animation")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
