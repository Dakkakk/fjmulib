import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
//    kotlin("jvm")
    alias(commons.plugins.kotlin.jvm)
//    alias(commons.plugins.ktor)
//    alias(commons.plugins.kotlin.serialization)
    alias(commons.plugins.compose.multiplatform)
}
group = "cn.luckcc.fjmu.lib"
version = "1.0-SNAPSHOT"
java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}
tasks.test {
    useJUnitPlatform()
}
task("sourceJar", Jar::class) {
    from(sourceSets.getByName("main").kotlin)
    archiveClassifier.set("sources")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "cn.luckcc.fjmu.lib"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    implementation(project(":lib"))
    implementation("org.jetbrains.kotlin:kotlin-test")
    implementation(commons.kotlinx.coroutine.core)
    implementation(libs.utils)
    implementation(libs.common)
//    implementation(commons.ktor.doubleReceive)
    implementation(commons.ktor.client.core)
    implementation(commons.ktor.client.okhttp)
//    implementation(commons.kotlinx.serialization.json)
//    implementation(commons.kotlinx.serialization.core)
//    implementation(commons.jsoup)
//    implementation(commons.exposed.core)
//    implementation(commons.exposed.jdbc)
//    implementation(commons.h2database)
//    implementation(commons.rhino)
    implementation(commons.logback)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)
    implementation(compose.desktop.currentOs)
}
