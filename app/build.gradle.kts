plugins {
    // Version-catalog alias for Android Application plugin
    alias(libs.plugins.android.application)

    // Google services (Firebase, etc.)
    id("com.google.gms.google-services")
}

android {
    namespace    = "com.example.quickcommerce"
    compileSdk   = 35

    defaultConfig {
        applicationId        = "com.example.quickcommerce"
        minSdk               = 26
        targetSdk            = 35
        versionCode          = 1
        versionName          = "1.0"
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

    buildFeatures {
        viewBinding = true
    }
}

val roomVersion = "2.5.1"

dependencies {
    // Core AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.activity:activity:1.8.2")
    testImplementation ("junit:junit:4.13.2")


    // Size SDP SSP
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    // Navigation Component
    val navVersion = "2.7.6"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Firebase (BOM + individual libs)
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Image loading & slideshow
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.0")

    // PhonePe SDK
    implementation ("com.razorpay:checkout:1.6.40")

    // Room Database (Java)
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    // Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // gson
    implementation ("com.google.code.gson:gson:2.10.1")

    // AndroidX Test dependencies for Instrumented Tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    //location
    implementation ("com.google.android.gms:play-services-location:21.0.1")
}




