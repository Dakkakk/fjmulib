plugins {
//    kotlin("jvm").version("1.9.20")
    alias(commons.plugins.kotlin.jvm) apply false
    alias(commons.plugins.ktor) apply false
    alias(commons.plugins.kotlin.serialization) apply false
    alias(commons.plugins.compose.multiplatform) apply false
}
