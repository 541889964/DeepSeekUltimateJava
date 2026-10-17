plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.deepseek.ultimate"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.deepseek.ultimate"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"
    }
    buildTypes { release { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    // 使用官方依赖，且必须是 compileOnly，不会打包进 APK
    compileOnly("de.robv.android.xposed:api:82")
}
