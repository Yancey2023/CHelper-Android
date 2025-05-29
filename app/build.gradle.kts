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
        versionCode = 58
        versionName = "0.2.45-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            // 为了减少软件体积，只兼容arm64-v8a架构
            // abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
            // abiFilters.add("x86")
            // abiFilters.add("x86_64")
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
        buildConfig = true
        viewBinding = true
    }

    ndkVersion = "27.2.12479018"

    kotlinOptions {
        jvmTarget = "17"
    }

    gradle.projectsEvaluated {
        tasks.withType<JavaCompile> {
            options.compilerArgs.add("-Xlint:unchecked")
            options.compilerArgs.add("-Xlint:deprecation")
        }
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
    // https://github.com/google/gson
    implementation("com.google.code.gson:gson:2.13.1")
    // https://github.com/boxbeam/Crunch
    implementation("com.github.Redempt:Crunch:2.0.3")
    // https://github.com/androidx/androidx
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    // https://github.com/ReactiveX/RxJava
    implementation("io.reactivex.rxjava3:rxjava:3.1.10")
    // https://github.com/ReactiveX/RxAndroid
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    // https://github.com/square/okhttp
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:okhttp-brotli")
    implementation("com.squareup.okhttp3:logging-interceptor")
    // https://github.com/square/retrofit
    implementation(platform("com.squareup.retrofit2:retrofit-bom:3.0.0"))
    implementation("com.squareup.retrofit2:retrofit")
    implementation("com.squareup.retrofit2:converter-gson")
    implementation("com.squareup.retrofit2:adapter-rxjava3")
    // https://github.com/getActivity/XXPermissions
    implementation("com.github.getActivity:XXPermissions:21.3")
    // https://github.com/getActivity/Toaster
    implementation("com.github.getActivity:Toaster:12.8")
    // https://github.com/getActivity/EasyWindow
    implementation("com.github.getActivity:EasyWindow:12.0")
    // https://www.umeng.com
    implementation("com.umeng.umsdk:common:9.8.4")
    implementation("com.umeng.umsdk:asms:1.8.7.2")
    // noinspection Aligned16KB
    implementation("com.umeng.umsdk:apm:2.0.3")
    // https://github.com/junit-team/junit4
    testImplementation("junit:junit:4.13.2")
    // https://github.com/androidx/androidx
    androidTestImplementation("androidx.test:core:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}