plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.altron.urovocustomerdemo"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.altron.urovocustomerdemo"
        minSdk = 27
        targetSdk = 36
        versionCode = 2
        versionName = String.format("07.00.04.%04X",versionCode)


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

base {
    archivesName.set("urovo-customer-demo-${android.defaultConfig.versionName}")
}

dependencies {
    implementation(fileTree("libs") {
        include("*.jar")
    })
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.zxing.android.embedded)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.journeyapps.zxing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}