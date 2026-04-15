import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
}

configure<LibraryExtension> {
    namespace = "com.guarantify.data"

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
            )
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    //Modules
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.util)

    implementation(libs.androidx.core.ktx)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    //Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    //WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.work)

    //JUnit5
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.platform.launcher)

    //Kotlin Test
    testImplementation(libs.jetbrains.kotlin.test)
    androidTestImplementation(libs.jetbrains.kotlin.test)

    //Mockk
    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockk.android)

    //Coroutines test
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    //WorkManager test
    androidTestImplementation(libs.work.testing)

    //JUnit4
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.junit)

    //DataStore
    implementation(libs.androidx.datastore.preferences)
}