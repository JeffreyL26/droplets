plugins {
    kotlin("jvm") version "2.0.21"
    application
}

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.12"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.json:json:20240303")
}

application {
    // Entry-Point: RelayServer.kt → main()
    mainClass.set("com.jbastudio.gofish.relay.RelayServerKt")
}

kotlin {
    jvmToolchain(17)
}
