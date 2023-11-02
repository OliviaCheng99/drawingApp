import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}



android {
    namespace = "com.example.drawApp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.drawApp"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//        configurations.all {
//            resolutionStrategy {
//                force("androidx.emoji2:emoji2-views-helper:1.3.0")
//                force("androidx.emoji2:emoji2:1.3.0")
//            }
//        } // code block to avoid some bugs? https://issuetracker.google.com/issues/295457468
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true // for compose view
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-testing:2.6.2")
    implementation("androidx.core:core-animation:1.0.0-rc01")
    implementation("androidx.annotation:annotation:1.7.0")
    implementation("androidx.test.ext:junit-ktx:1.1.5")

    //tests stuff
//    testImplementation("junit:junit:4.13.2")
//    testImplementation("androidx.arch.core:core-testing:2.2.0")
//    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    testImplementation("androidx.room:room-testing:2.6.0")
    implementation("org.mockito:mockito-core:5.6.0")
    implementation("com.google.android.datatransport:transport-runtime:3.1.9")
    testImplementation("org.mockito:mockito-core:5.6.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    androidTestImplementation("org.mockito:mockito-android:5.6.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    val coroutinesVersion = "1.7.3"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")


    testImplementation("org.hamcrest:hamcrest-library:2.2")
//    testImplementation ("org.mockito:mockito-core:2.25.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
// Additional dependencies for Jetpack Compose UI testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
// Dependencies for Fragment testing
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.2")
// Mockito for Android to mock Android-specific classes (like ViewModel)
    androidTestImplementation("org.mockito:mockito-android:5.6.0")

    //to get livedata + viewmodel stuff
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    //Fragment stuff
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // compose stuff
    implementation("androidx.compose.ui:ui-tooling-android:1.5.4")
    implementation("androidx.compose.material:material-android:1.5.4")
    implementation("androidx.compose.ui:ui-graphics-android:1.5.4")
    implementation("androidx.compose.ui:ui-android:1.5.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")

    // navigation stuff
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5") // stable version
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    // Room stuff
    ksp("androidx.room:room-compiler:2.6.0")
    implementation("androidx.room:room-common:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    implementation("androidx.room:room-runtime:2.6.0")

    // Google Auth
    implementation("com.firebaseui:firebase-ui-auth:8.0.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-analytics")

    // ktor client
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-logging:2.3.5")
    implementation("io.ktor:ktor-client-serialization:2.3.5")
}