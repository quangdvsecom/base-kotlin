import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.daggerhilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.ksp)
    kotlin("plugin.serialization") version "2.0.10"
    id("kotlin-parcelize")
    kotlin("kapt")
    id("com.google.gms.google-services")

}
val keystoreProperties = Properties().apply {
    load(FileInputStream(rootProject.file("keystore.properties")))
}
val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}
android {
    namespace = "com.el.mybasekotlin"
    compileSdk = 35
    packaging {
    }
    defaultConfig {
        applicationId = "com.el.mybasekotlin"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] =
                    "$projectDir/schemas"
            }
        }
        val buildDate = SimpleDateFormat("ddMMyy_HHmm").format(Date())
        setProperty("archivesBaseName", "Demo_base_kotlin_build_${versionName}_${buildDate}")

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a")) // Kotlin syntax
        }
    }
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    buildTypes {
        debug {
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    flavorDimensions("default")
    productFlavors {
        create("dev") {
            manifestPlaceholders["appName"] = "@string/app_name_Dev"
            applicationId = "com.el.mybasekotlin.dev"
            buildConfigField("String", "API_DOMAIN", "\"https://hybrid-event.vn\"")
//            buildConfigField("String", "API_DOMAIN", "\"https://hybrid-event.vn\"")
            buildConfigField ("String", "API_KEY", "\"${localProperties.getProperty("API_KEY")}\"")
        }
        create("product") {
            manifestPlaceholders["appName"] = "@string/app_name"
            applicationId = "com.el.mybasekotlin"
//            buildConfigField("String", "API_DOMAIN", "\"https://dev.hybrid-event.vn\"")
            buildConfigField("String", "API_DOMAIN", "\"https://hybrid-event.vn\"")
            buildConfigField ("String", "API_KEY", "\"${localProperties.getProperty("API_KEY")}\"")
        }
    }
    tasks.register("printVersionName") {
        doLast {
            println(android.defaultConfig.versionName)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true

    }
    // Các cấu hình khác
    packagingOptions {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/NOTICE.txt"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.databinding.compiler.common)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.android.testing)
    // Dagger - Hilt
    implementation(libs.hilt.android)
    implementation(libs.play.services.mlkit.face.detection)
    ksp(libs.hilt.android.compiler)
    // Coroutines
//    implementation(libs.kotlinx.coroutines.android)
    //test
//    testImplementation(libs.junit)
//    testImplementation(libs.junit.jupiter.params)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.multidex)
    testImplementation(libs.gson.v2101)
    androidTestImplementation ("androidx.fragment:fragment-testing:1.8.6")
//    androidTestImplementation( "androidx.arch.core:core-testing:2.2.0")
    testImplementation (libs.junit)
    // Kotlin Coroutines testing
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation (libs.mockito.kotlin)
    androidTestImplementation (libs.mockito.android)
    testImplementation ("app.cash.turbine:turbine:1.0.0")
    androidTestImplementation(project(":app"))
    androidTestImplementation(project(":app"))
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48") // Thay đổi phiên bản nếu cần
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.48")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.material3)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.scalars)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)
//    chuckerteam
    debugImplementation (libs.library.v410)
    releaseImplementation (libs.library.no.op)
    // Swipe to refresh
    implementation(libs.androidx.swiperefreshlayout)
    // Jetpack navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
//    implementation(libs.androidx.navigation.dynamic.features.fragment)
//    api(libs.androidx.navigation.fragment.ktx)
//Glide
    implementation (libs.glide)



    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    //Gson
    implementation(libs.gson)
//Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation(libs.com.google.firebase.firebase.analytics)

    /**
     * Camera view
     */
    implementation("com.otaliastudios:cameraview:2.7.2")

}