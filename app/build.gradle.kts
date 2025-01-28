import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "yancey.chelper"
    compileSdk = 35

    defaultConfig {
        applicationId = "yancey.chelper"
        minSdk = 24
        targetSdk = 35
        versionCode = 48
        versionName = "0.2.35-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            // 为了减少软件体积，只兼容arm架构
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
//            abiFilters.add("x86")
//            abiFilters.add("x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CHelper-Core/CHelper-Android/CMakeLists.txt")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    ndkVersion = "27.2.12479018"

    kotlinOptions {
        jvmTarget = "17"
    }

}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
if (keystorePropertiesFile.exists()) {
    val keystoreProperties = Properties()
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    android.signingConfigs {
        create("sign") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String

            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }
    android.buildTypes["release"].signingConfig = android.signingConfigs["sign"]
    android.buildTypes["debug"].signingConfig = android.signingConfigs["sign"]
    android.applicationVariants.all {
        outputs.all {
            if (this is ApkVariantOutputImpl) {
                outputFileName = "CHelper-${android.defaultConfig.versionName}.Apk"
            }
        }
    }
}

dependencies {
    // https://github.com/princekin-f/EasyFloat
    implementation(project(":easyfloat"))
    // https://github.com/google/gson
    implementation("com.google.code.gson:gson:2.11.0")
    // https://github.com/boxbeam/Crunch
    implementation("com.github.Redempt:Crunch:2.0.3")
    // https://github.com/androidx/androidx
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.core:core-ktx:1.13.0")
    // https://github.com/square/okhttp
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:okhttp-brotli")
    // https://github.com/junit-team/junit4
    testImplementation("junit:junit:4.13.2")
    // https://github.com/androidx/androidx
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}