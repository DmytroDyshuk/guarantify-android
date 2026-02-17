plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.ksp)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {

    //Coroutines
    implementation(libs.kotlinx.coroutines.core)

    //Hilt
    implementation(libs.hilt.core)
    ksp(libs.hilt.compiler)

}