plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
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

    buildFeatures{
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

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-testing:2.6.1")
    implementation("androidx.core:core-animation:1.0.0-rc01")
    implementation("androidx.room:room-common:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    implementation("androidx.annotation:annotation:1.7.0")
    implementation("androidx.test.ext:junit-ktx:1.1.5")

    //tests stuff
//    testImplementation("junit:junit:4.13.2")
//    testImplementation("androidx.arch.core:core-testing:2.2.0")
//    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    testImplementation("androidx.room:room-testing:2.6.0")
    implementation("org.mockito:mockito-core:5.3.1")
    testImplementation( "org.mockito:mockito-core:4.0.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    androidTestImplementation("org.mockito:mockito-android:2.25.0")
//    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    val coroutinesVersion = "1.3.0-M1"
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")


    testImplementation ("org.hamcrest:hamcrest-library:2.1")
//    testImplementation ("org.mockito:mockito-core:2.25.0")

    testImplementation ("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test:rules:1.2.0")


    //to get livedata + viewmodel stuff
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    //Fragment stuff
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // compose stuff
    implementation("androidx.compose.ui:ui-tooling-android:1.5.3")
    implementation("androidx.compose.material:material-android:1.5.3")
    implementation("androidx.compose.ui:ui-graphics-android:1.5.3")
    implementation("androidx.compose.ui:ui-android:1.5.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.1")

    // navigation stuff
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3") // stable version
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // Room stuff
    ksp("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-common:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    implementation("androidx.room:room-runtime:2.5.2")


}