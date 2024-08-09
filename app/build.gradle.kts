import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.android.build.gradle.internal.cxx.configure.abiOf
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "yancey.chelper"
    compileSdk = 34

    defaultConfig {
        applicationId = "yancey.chelper"
        minSdk = 24
        targetSdk = 34
        versionCode = 37
        versionName = "0.2.24-beta"

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
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    ndkVersion = "27.0.12077973"

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
    implementation(project(":easyfloat"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.Redempt:Crunch:2.0.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}