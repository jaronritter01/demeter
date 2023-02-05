plugins {
	// java
	groovy
	id("org.springframework.boot") version "3.0.2"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.finalproject"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("io.jsonwebtoken:jjwt:0.2")
	implementation("javax.xml.bind:jaxb-api:2.2.4")
	// Logging
	implementation("org.springframework.boot:spring-boot-starter-log4j2:3.0.2")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.spockframework:spock-core:2.4-M1-groovy-4.0")
	testImplementation("org.spockframework:spock-spring:2.4-M1-groovy-4.0")
	testImplementation("org.apache.groovy:groovy-all:4.0.8")
	testImplementation("com.h2database:h2:2.1.214")

}

configurations.implementation {
	exclude (group = "org.springframework.boot", module = "spring-boot-starter-logging")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks {
	bootJar {
		launchScript()
	}
}