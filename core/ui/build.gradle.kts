import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

configure<LibraryExtension> {
    namespace = "com.guarantify.ui"

    buildFeatures {
        compose = true
    }
}

dependencies {
    //Modules
    implementation(projects.core.util)

    //Core
    implementation(libs.androidx.core.ktx)

    //Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)

    //Fonts
    implementation(libs.androidx.ui.text.google.fonts)

    //M3
    implementation(libs.androidx.material3)

    //Compose Preview
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.tooling.preview)

    //Lottie
    implementation(libs.lottie.compose)

    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}