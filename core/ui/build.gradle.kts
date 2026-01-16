import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

configure<LibraryExtension> {
    namespace = "com.guarantify.ui"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.core.util)

    implementation(libs.androidx.core.ktx)

    //Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.tooling.preview)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //Lottie
    implementation(libs.lottie.compose)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}