plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.contactlist4"
    compileSdk = 34  // ✅ Changed from 35 to 34 for compatibility

    defaultConfig {
        applicationId = "com.example.contactlist4"
        minSdk = 24
        targetSdk = 34  // ✅ Changed from 35 to 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
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

dependencies {
    // ✅ Added explicit Google Play Services dependencies
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // ✅ Added explicit AndroidX dependencies
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.0")  // ✅ Fix for EdgeToEdge
    implementation("androidx.core:core-ktx:1.9.0")

    // ✅ Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // ✅ MultiDex Support (if needed)
    implementation("androidx.multidex:multidex:2.0.1")
}
