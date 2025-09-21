plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jetbrains.kotlin.plugin.noarg") version "1.9.25"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
}

noArg {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
}

group = "com.leita"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations.compileOnly {
	extendsFrom(configurations.annotationProcessor.get())
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation ("org.springframework.cloud:spring-cloud-starter-openfeign:4.2.1")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

	implementation("mysql:mysql-connector-java:8.0.33")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.hibernate:hibernate-core:6.5.0.Final")
	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

	implementation("org.springframework.boot:spring-boot-starter-mail")

	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	implementation("org.springframework.boot:spring-boot-starter-webflux")

	testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
	testImplementation("org.assertj:assertj-core:3.25.3")

    implementation("com.oracle.oci.sdk:oci-java-sdk-objectstorage:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-identity:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common-httpclient-jersey3:3.38.0")

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")

    implementation("org.glassfish.jersey.core:jersey-client:3.1.3")
    implementation("org.glassfish.jersey.inject:jersey-hk2:3.1.3")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.1.3")
    implementation("org.glassfish.jersey.connectors:jersey-apache-connector:3.1.3")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
