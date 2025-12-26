plugins {
    `java-library`
    `maven-publish`
    signing
}

// Project metadata from gradle.properties
group = property("group") as String
version = property("version") as String

val projectName: String by project
val projectDescription: String by project
val projectUrl: String by project
val projectScmUrl: String by project
val licenseName: String by project
val licenseUrl: String by project
val developerName: String by project
val developerEmail: String by project
val developerOrganization: String by project
val developerOrganizationUrl: String by project

repositories {
    // Internal Nexus repository for dependencies
    maven {
        name = "LeqeeNexus"
        url = uri("https://nexus.leqeegroup.com/repository/public/")
        credentials {
            username = findProperty("leqeeNexusUsername") as String? ?: System.getenv("NEXUS_USERNAME")
            password = findProperty("leqeeNexusPassword") as String? ?: System.getenv("NEXUS_PASSWORD")
        }
        // Allow insecure protocol if needed (not recommended for production)
        // isAllowInsecureProtocol = false
    }

    // mavenCentral()
}

dependencies {
    // Main dependencies
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.apache.logging.log4j:log4j-api:2.24.3")
    implementation("io.github.sinri:keel-logger-api:5.0.0-rc.20.1")

    // Test dependencies
    testImplementation("io.vertx:vertx-junit5:5.0.6")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks.compileTestJava {
    options.encoding = "UTF-8"
    options.release.set(17)
}

// Configure resources (exclude config.properties like Maven)
tasks.processResources {
    exclude("config.properties")
}

// Configure test task
tasks.test {
    useJUnitPlatform()
    include("io/github/sinri/keel/logger/ext/**/*Test.class")
    include("io/github/sinri/keel/logger/ext/**/*TestCase.class")
}

// Configure JavaDoc
tasks.javadoc {
    options.encoding = "UTF-8"
    if (options is StandardJavadocDocletOptions) {
        val stdOptions = options as StandardJavadocDocletOptions
        stdOptions.charSet = "UTF-8"
        stdOptions.docEncoding = "UTF-8"
        stdOptions.memberLevel = JavadocMemberLevel.PROTECTED
        stdOptions.docTitle = "$projectName $version Document"
        stdOptions.windowTitle = "$projectName $version Document"
        stdOptions.addBooleanOption("html5", true)
        stdOptions.addStringOption("Xdoclint:-missing", "-quiet") // 提示缺失的注释
    }
}

// Publishing configuration
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set(projectName)
                description.set(projectDescription)
                url.set(projectUrl)

                licenses {
                    license {
                        name.set(licenseName)
                        url.set(licenseUrl)
                    }
                }

                developers {
                    developer {
                        name.set(developerName)
                        email.set(developerEmail)
                        organization.set(developerOrganization)
                        organizationUrl.set(developerOrganizationUrl)
                    }
                }

                scm {
                    url.set(projectScmUrl)
                }
            }
        }
    }

    repositories {
        // Internal Nexus repositories
        maven {
            name = "Internal"
            url = if (version.toString().endsWith("SNAPSHOT")) {
                uri("https://nexus.leqeegroup.com/repository/snapshots/")
            } else {
                uri("https://nexus.leqeegroup.com/repository/releases/")
            }
            credentials {
                username = findProperty("leqeeNexusUsername") as String? ?: System.getenv("NEXUS_USERNAME")
                password = findProperty("leqeeNexusPassword") as String? ?: System.getenv("NEXUS_PASSWORD")
            }
        }

        // Maven Central (OSSRH)
        maven {
            name = "Release"

            if (version.toString().endsWith("SNAPSHOT")) {
                url = uri("https://nexus.leqeegroup.com/repository/snapshots/")
                credentials {
                    username = findProperty("leqeeNexusUsername") as String? ?: System.getenv("NEXUS_USERNAME")
                    password = findProperty("leqeeNexusPassword") as String? ?: System.getenv("NEXUS_PASSWORD")
                }
            } else {
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME")
                    password = findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD")
                }
            }

        }
    }
}

// Signing configuration
signing {
    // Only sign if not a SNAPSHOT and signing credentials are available
    setRequired({
        !version.toString().endsWith("SNAPSHOT") && gradle.taskGraph.hasTask("publish")
    })
    sign(publishing.publications["mavenJava"])
}

// Custom tasks for publishing to specific repositories
tasks.register("publishToInternal") {
    group = "publishing"
    description = "Publish to internal Nexus repository"
    dependsOn("publishMavenJavaPublicationToInternalRepository")
}

tasks.register("publishToRelease") {
    group = "publishing"
    description = "Publish to Maven Central (OSSRH)"
    dependsOn("publishMavenJavaPublicationToReleaseRepository")
}

