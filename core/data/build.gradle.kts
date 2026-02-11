import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
}

configure<LibraryExtension> {
    namespace = "com.guarantify.data"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    //Modules
    implementation(projects.core.domain)
    implementation(projects.core.common)

    implementation(libs.androidx.core.ktx)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    //JUnit5
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.platform.launcher)

    //Mockk
    testImplementation(libs.mockk)

    //Coroutines test
    testImplementation(libs.kotlinx.coroutines.test)
}