plugins {
    id("java")
    id("com.github.bjornvester.xjc") version "1.9.0"
    id("maven-publish")
}

group = "nl.bliksemlabs"
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
    xsdDir.set(layout.projectDirectory.dir("src/main/resources/infoplus"))
    bindingFiles.setFrom(layout.projectDirectory.dir("src/main/resources").asFileTree.matching { include("**/*.xjb") })
    includes.set(
        listOf(
            "dvs/NDOV_Med_DVSPush_DVSPushExport-v5.0.0.wsdl",
            "das/NDOV_Med_DASPush_DASPushExport-v1.0.0.wsdl",
            "rit/RITPush_RITPushExport-v5.wsdl"
        )
    )
    options.add("-wsdl")
}

java {
    withSourcesJar()
    withJavadocJar()
}

// Publish to Github Packages
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("InfoPlus Model For Java")
                description.set("Java model/classes for InfoPlus messages")
                url.set("https://github.com/joelhaasnoot/infoplus-java-model")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("joelhaasnoot")
                        name.set("Joel Haasnoot")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/joelhaasnoot/infoplus-java-model.git")
                    developerConnection.set("scm:git:ssh://github.com/joelhaasnoot/infoplus-java-model.git")
                    url.set("https://github.com/joelhaasnoot/infoplus-java-model")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/joelhaasnoot/infoplus-java-model")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
