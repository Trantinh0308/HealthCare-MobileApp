plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.healthcare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.healthcare"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "x86", "armeabi"))
        }
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation(fileTree(mapOf(
        "dir" to "C:\\Users\\trant\\Desktop\\zalolib",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.squareup.picasso:picasso:2.71828")

    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation ("com.github.ZEGOCLOUD:zego_uikit_prebuilt_live_streaming_android:+")
    implementation ("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:+")

    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    //noinspection GradleDependency
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation("com.google.firebase:firebase-messaging:24.0.3")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation ("com.google.android.gms:play-services-fitness:21.2.0")
    implementation ("com.google.mlkit:face-detection:16.1.7")

    implementation ("androidx.camera:camera-core:1.4.0")
    // CameraX Camera2 (for backward compatibility with Camera2 API)
    implementation ("androidx.camera:camera-camera2:1.4.0")
    // CameraX lifecycle (to bind camera lifecycle)
    implementation ("androidx.camera:camera-lifecycle:1.4.0")
    implementation ("com.google.guava:guava:30.1-android")
    implementation ("androidx.camera:camera-view:1.0.0-alpha31")
    implementation ("androidx.biometric:biometric:1.2.0-alpha05")
    implementation("commons-codec:commons-codec:1.14")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
}