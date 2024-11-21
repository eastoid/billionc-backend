import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm") version "2.0.20"
	kotlin("plugin.spring") version "2.0.20"
	kotlin("plugin.serialization") version "2.0.20"
}

group = "billionc"
version = "1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.atlassian:onetime:2.1.1")

	implementation("de.huxhorn.sulky:de.huxhorn.sulky.ulid:8.3.0")
	implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.3.5")

	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:3.3.5")
	implementation("org.postgresql:r2dbc-postgresql:1.0.5.RELEASE")

	implementation("org.springframework.boot:spring-boot-starter-security:3.3.5")
	implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.5")
	implementation("org.springframework.boot:spring-boot-starter-rsocket:3.3.5")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.3")
	implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.20")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")

	implementation("org.springframework.boot:spring-boot-starter-actuator:3.3.5")
	implementation("io.micrometer:micrometer-core:1.13.5")

	testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.5")
	testImplementation("io.projectreactor:reactor-test:3.6.10")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.2")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
