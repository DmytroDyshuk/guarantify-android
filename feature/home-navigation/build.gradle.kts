import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

configure<LibraryExtension> {
    namespace = "com.guarantify.home_navigation"
}

dependencies {
    //Modules
    implementation(projects.feature.warranties)
    implementation(projects.feature.insights)
    implementation(projects.feature.settings)
    implementation(projects.core.navigation)

    implementation(libs.androidx.core.ktx)

    //Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling.preview)

    //Serialization
    implementation(libs.kotlinx.serialization.json)

    //Navigation
    implementation(libs.navigation.compose)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}