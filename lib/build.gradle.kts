
plugins {
    alias(commons.plugins.kotlin.jvm)
    alias(commons.plugins.ktor)
    alias(commons.plugins.kotlin.serialization)
    `maven-publish`
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
publishing {
    publications {
        val providedArtifactId = project.name

        create(providedArtifactId, MavenPublication::class.java) {
//            from(components.getByName("release"))
            groupId = "cn.luckcc.fjmu.lib"
            artifactId = "fjmuHelperLib"
            version = "0.0.1fix3"
            artifact(tasks.getByName("sourceJar"))

            from(components.getByName("kotlin"))
        }
    }
}
////////////////////////////////////


dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation(commons.kotlinx.coroutine.core)
    implementation(libs.utils)
    implementation(libs.common)
    implementation(commons.ktor.doubleReceive)
    implementation(commons.ktor.client.core)
    implementation(commons.ktor.client.okhttp)
    implementation(commons.kotlinx.serialization.json)
    implementation(commons.kotlinx.serialization.core)
    implementation(commons.jsoup)
    implementation(commons.exposed.core)
    implementation(commons.exposed.jdbc)
    implementation(commons.h2database)
    implementation(commons.rhino)
//    testImplementation()
}