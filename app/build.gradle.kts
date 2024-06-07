import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.rk.xededitor"
    compileSdk = 34

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    val filePath = "/home/rohit/signing.properties"
    val file = File(filePath)

    if (file.exists()) {
        signingConfigs {
            create("release") {
                val propertiesFile = rootProject.file("/home/rohit/signing.properties")
                val properties = Properties()
                properties.load(propertiesFile.inputStream())
                keyAlias = properties["keyAlias"] as String
                keyPassword = properties["keyPassword"] as String
                storeFile = file(properties["storeFile"] as String)
                storePassword = properties["storePassword"] as String
            }
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                isCrunchPngs = false
                signingConfig = signingConfigs.getByName("release")
            }
        }
    } else {
        println("Not a local environment, skipping signing")
        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                isCrunchPngs = false
            }
        }
    }

    defaultConfig {
        applicationId = "com.rk.xededitor"
        minSdk = 26
        targetSdk = 33
        versionCode = 16
        versionName = "1.1.6"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation(platform("io.github.Rosemoe.sora-editor:bom:0.23.4"))
    implementation("io.github.Rosemoe.sora-editor:editor")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.22.0")
    implementation("io.github.Rosemoe.sora-editor:language-textmate")
}
