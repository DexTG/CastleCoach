plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.room") version "2.6.1"
}
room {
    schemaDirectory("$projectDir/schemas")
}

android {
namespace = "com.castlecoach.app"
compileSdk = 35


defaultConfig {
applicationId = "com.castlecoach.app"
minSdk = 26
targetSdk = 35
versionCode = 1
versionName = "1.0.0"


vectorDrawables { useSupportLibrary = true }
}


buildTypes {
release {
isMinifyEnabled = true
proguardFiles(
getDefaultProguardFile("proguard-android-optimize.txt"),
"proguard-rules.pro"
)
}
debug { isMinifyEnabled = false }
}


compileOptions {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions { jvmTarget = "17" }


buildFeatures { compose = true }
composeOptions {
kotlinCompilerExtensionVersion = "1.5.14"
}
packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}


dependencies {

val room = "2.6.1"
implementation("androidx.room:room-ktx:$room")
kapt("androidx.room:room-compiler:$room") // if using KAPT
// OR KSP:
// ksp("androidx.room:room-compiler:$room")

// Java Time backport for older devices (optional if minSdk >= 26)
implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
implementation("androidx.health.connect:connect-client:1.0.0-alpha11")

val composeBom = platform("androidx.compose:compose-bom:2024.09.03")
implementation(composeBom)
androidTestImplementation(composeBom)


implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")


implementation("androidx.core:core-ktx:1.13.1")
implementation("androidx.activity:activity-compose:1.9.2")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
implementation("androidx.navigation:navigation-compose:2.8.2")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-tooling-preview")
debugImplementation("androidx.compose.ui:ui-tooling")

implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
implementation("androidx.work:work-runtime-ktx:2.9.1")
implementation("androidx.work:work-runtime-ktx:2.9.0")
implementation("com.google.android.material:material:1.12.0")

// Preferences
implementation("androidx.datastore:datastore-preferences:1.1.1")
}
