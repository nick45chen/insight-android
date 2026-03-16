import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val signingProps = Properties().apply {
    val file = rootProject.file("signing.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

android {
    namespace = "com.example.insight_android"
    compileSdk {
        version = release(36)
    }

    val targetPackageName = signingProps.getProperty("targetPackageName", "com.example.target")

    defaultConfig {
        applicationId = "com.example.insight_android"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TARGET_PACKAGE_NAME", "\"$targetPackageName\"")
        manifestPlaceholders["sharedUserId"] = targetPackageName
    }

    val keystoreFile = file(signingProps.getProperty("storeFile", "release.keystore"))
    val hasSigningConfig = signingProps.getProperty("storePassword", "").isNotEmpty()
        && signingProps.getProperty("keyAlias", "").isNotEmpty()
        && keystoreFile.exists()

    if (hasSigningConfig) {
        signingConfigs {
            create("target") {
                storeFile = file(signingProps.getProperty("storeFile", "release.keystore"))
                storePassword = signingProps.getProperty("storePassword", "")
                keyAlias = signingProps.getProperty("keyAlias", "")
                keyPassword = signingProps.getProperty("keyPassword", "")
            }
        }
    }

    buildTypes {
        debug {
            if (hasSigningConfig) {
                signingConfig = signingConfigs.getByName("target")
            }
        }
        release {
            isMinifyEnabled = false
            if (hasSigningConfig) {
                signingConfig = signingConfigs.getByName("target")
            }
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}