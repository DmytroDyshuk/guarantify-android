import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

configure<LibraryExtension> {
    namespace = "com.guarantify.navigation"
}

dependencies {
    //Core
    implementation(libs.androidx.core.ktx)

    //Serialization
    implementation(libs.kotlinx.serialization.json)
}