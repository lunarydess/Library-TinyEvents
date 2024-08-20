import java.nio.charset.StandardCharsets

plugins {
    id("idea")

    id("java")
    id("java-library")

    id("me.champeau.jmh") version "0.7.2"
}

group = "zip.luzey"
version = "1.0.0-develop"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor(
        group = "org.jetbrains",
        name = "annotations",
        version = properties["dep_ann-jbr"].toString()
    )
    implementation(
        group = "org.jetbrains",
        name = "annotations",
        version = properties["dep_ann-jbr"].toString()
    )

    testImplementation(
        group = "org.junit.jupiter",
        name = "junit-jupiter-api",
        version = properties["dep_test-junit"].toString()
    )
    testRuntimeOnly(
        group = "org.junit.jupiter",
        name = "junit-jupiter-engine",
        version = properties["dep_test-junit"].toString()
    )

    jmh(
        group = "org.openjdk.jmh",
        name = "jmh-core",
        version = properties["dep_bench-jmh"].toString()
    )
    jmhAnnotationProcessor(
        group = "org.openjdk.jmh",
        name = "jmh-generator-annprocess",
        version = properties["dep_bench-jmh"].toString()
    )
    jmh(
        group = "org.openjdk.jmh",
        name = "jmh-generator-annprocess",
        version = properties["dep_bench-jmh"].toString()
    )
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain.languageVersion = JavaLanguageVersion.of(JavaVersion.VERSION_17.toString())
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
    options.encoding = StandardCharsets.UTF_8.toString()
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = 1
    failFast = false
}


tasks.withType<AbstractArchiveTask> {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}
