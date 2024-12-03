import java.math.MathContext

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("checkstyle")
}

group = "faang.school"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    /**
     * Spring boot starters
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
    implementation("org.redisson:redisson-spring-boot-starter:3.16.2")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.kafka:spring-kafka")

    /**
     * Database
     */
    implementation("org.liquibase:liquibase-core")
    implementation("redis.clients:jedis:4.3.2")
    runtimeOnly("org.postgresql:postgresql")

    /**
     * AmazonS3
     */
    implementation("io.github.fuquanming:fqm-file-amazons3:1.0.9")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.772")
    /**
     * Change Image Dimension
     */
    implementation("org.imgscalr:imgscalr-lib:4.2")

    /**
     * Utils & Logging
     */
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
    implementation("org.springframework.retry:spring-retry:2.0.9")

    /**
     * OpenAPI & swagger dependencies
     *   http://localhost:8081/swagger-ui/index.html - swagger ui
     *   http://localhost:8081/v3/api-docs - docs in JSON format
     */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.3")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.15")
    implementation("io.springfox:springfox-boot-starter:3.0.0")

    /**
     * Test containers
     */
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation ("org.testcontainers:kafka")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.github.hakky54:logcaptor:2.9.3")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    /**
     * Jacoco
     */
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}

/**
 * Jacoco
 */
val jacocoInclude = listOf(
    "**/controller/**",
    "**/service/**",
    "**/validator/**",
    "**/mapper/**"
)

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("$buildDir/reports/jacoco"))
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("$buildDir/reports/jacoco"))
    }

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            include(jacocoInclude)
        }
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            classDirectories.setFrom(
                sourceSets.main.get().output.asFileTree.matching {
                    include(jacocoInclude)
                }
            )
            enabled = true
            limit {
                minimum = BigDecimal(0.7).round(MathContext(1))
            }
        }
    }
}


checkstyle {
    toolVersion = "10.20.0"
    configFile = file("${rootDir}/src/main/resources/checkstyle/checkstyle.xml")
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
