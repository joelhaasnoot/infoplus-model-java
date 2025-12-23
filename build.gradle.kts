plugins {
    id("java")
    id("com.github.bjornvester.xjc") version "1.9.0"
}

group = "nl.openov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // JAXB runtime for unmarshalling
    testImplementation("org.glassfish.jaxb:jaxb-runtime:4.0.6")

    // Rewrite
    implementation("io.github.threeten-jaxb:threeten-jaxb-core:2.2.0") // This version is for Jakarta
}

tasks.test {
    useJUnitPlatform()
}

xjc {
    xsdDir.set(layout.projectDirectory.dir("src/main/resources/infoplus/rit"))
    bindingFiles.setFrom(layout.projectDirectory.dir("src/main/resources").asFileTree.matching { include("**/*.xjb") })
    includes.set(listOf("RITPush_RITPushExport-v5.wsdl"))
    options.add("-wsdl")
}
