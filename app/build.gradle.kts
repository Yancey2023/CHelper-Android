import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "yancey.chelper"
    compileSdk = 36

    defaultConfig {
        applicationId = "yancey.chelper"
        minSdk = 24
        targetSdk = 36
        versionCode = 64
        versionName = "0.3.5-beta"

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

    sourceSets.all {
        jniLibs.srcDirs("libs")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    ndkVersion = "28.1.13356709"

    kotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
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
    android.buildTypes.all {
        signingConfig = android.signingConfigs["sign"]
    }
    android.applicationVariants.all {
        outputs.all {
            if (this is ApkVariantOutputImpl) {
                outputFileName = "CHelper-${android.defaultConfig.versionName}.apk"
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
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    // https://github.com/ReactiveX/RxJava
    implementation("io.reactivex.rxjava3:rxjava:3.1.10")
    // https://github.com/ReactiveX/RxAndroid
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    // https://github.com/square/okhttp
    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.0.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:okhttp-brotli")
    implementation("com.squareup.okhttp3:logging-interceptor")
    // https://github.com/square/retrofit
    implementation(platform("com.squareup.retrofit2:retrofit-bom:3.0.0"))
    implementation("com.squareup.retrofit2:retrofit")
    implementation("com.squareup.retrofit2:converter-gson")
    implementation("com.squareup.retrofit2:adapter-rxjava3")
    // https://github.com/getActivity/XXPermissions
    implementation("com.github.getActivity:XXPermissions:23.0")
    // https://github.com/getActivity/Toaster
    implementation("com.github.getActivity:Toaster:13.0")
    // https://github.com/getActivity/EasyWindow
    implementation("com.github.getActivity:EasyWindow:12.0")
    // https://www.umeng.com
    implementation("com.umeng.umsdk:common:9.8.5")
    implementation("com.umeng.umsdk:asms:1.8.7.2")
    // noinspection Aligned16KB
    implementation("com.umeng.umsdk:apm:2.0.4")
    // https://github.com/junit-team/junit4
    testImplementation("junit:junit:4.13.2")
    // https://github.com/androidx/androidx
    androidTestImplementation("androidx.test:core:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}