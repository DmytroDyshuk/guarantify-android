plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    //Modules
    implementation(projects.core.common)

    implementation(libs.jakarta.inject)
    implementation(libs.kotlinx.coroutines.core)
}