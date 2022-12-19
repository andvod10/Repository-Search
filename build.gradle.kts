import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Spring Boot 3 isn't stable with some libraries yet
    // https://stackoverflow.com/questions/71549614/springfox-type-javax-servlet-http-httpservletrequest-not-present
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"
    id("org.openapi.generator") version "6.2.1"
}

group = "com.tui"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    //cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.5")

    //swagger
    implementation("org.springdoc:springdoc-openapi-ui:1.6.14")
    runtimeOnly("org.springdoc:springdoc-openapi-kotlin:1.6.14")

    //github
    implementation("org.kohsuke:github-api:1.313")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName.set("vcs-repository-search.jar")
}

tasks.openApiGenerate {
    generatorName.set("spring")
    generatorName.set("kotlin")
    apiPackage.set("com.tui.vcsrepositorysearch.api")
    modelPackage.set("com.tui.vcsrepositorysearch.model")
    configOptions.put("delegatePattern", "false")
    inputSpec.set("$rootDir/src/main/resources/static/repository-search.yml")
    outputDir.set("$buildDir/generated")
}
