import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

configure<LibraryExtension> {
    namespace = "com.guarantify.util"
}

dependencies {
    //Modules
    implementation(projects.core.common)

    //Core
    implementation(libs.androidx.core.ktx)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.exifinterface)

    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}