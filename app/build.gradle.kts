plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.todoappv2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.todoappv2"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/ASL2.0"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"
            excludes += "META-INF/versions/9/module-info.class"
            excludes += "META-INF/maven/**"
            excludes += "META-INF/proguard/**"
            excludes += "META-INF/com.google.protobuf/**"
            excludes += "META-INF/com.google.api.grpc/**"
            excludes += "META-INF/com.google.api.client/**"
            excludes += "META-INF/com.google.auth/**"
            excludes += "META-INF/com.google.cloud/**"
            excludes += "META-INF/com.google.common/**"
            excludes += "META-INF/com.google.guava/**"
            excludes += "META-INF/com.google.protobuf/**"
            excludes += "META-INF/com.google.rpc/**"
            excludes += "META-INF/com.google.type/**"
            excludes += "META-INF/com.google.api/**"
            excludes += "META-INF/com.google.iam/**"
            excludes += "META-INF/com.google.longrunning/**"
            excludes += "META-INF/com.google.rpc/**"
            excludes += "META-INF/com.google.type/**"
            excludes += "META-INF/com.google.api.services.**"
            excludes += "META-INF/com.google.api.client.**"
            excludes += "META-INF/com.google.api.grpc.**"
            excludes += "META-INF/com.google.auth.**"
            excludes += "META-INF/com.google.cloud.**"
            excludes += "META-INF/com.google.common.**"
            excludes += "META-INF/com.google.guava.**"
            excludes += "META-INF/com.google.protobuf.**"
            excludes += "META-INF/com.google.rpc.**"
            excludes += "META-INF/com.google.type.**"
            excludes += "META-INF/com.google.api.**"
            excludes += "META-INF/com.google.iam.**"
            excludes += "META-INF/com.google.longrunning.**"
            excludes += "META-INF/com.google.rpc.**"
            excludes += "META-INF/com.google.type.**"
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Room components
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-common:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // CardView
    implementation("androidx.cardview:cardview:1.0.0")
    
    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    
    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    
    // Google AI Client Library
    implementation("com.google.cloud:google-cloud-aiplatform:3.38.0")
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.http-client:google-http-client-gson:1.43.3")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}