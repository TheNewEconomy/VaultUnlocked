plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "9.0.0-rc3"
}

group = "net.milkbowl.vault"
val vuApiVersion: String = "2.15"
val vuRelVersion: String = ".1"
version = vuApiVersion.plus(vuRelVersion)
description = "VaultUnlocked is a Chat, Permissions & Economy API to allow plugins to more easily" +
        " hook into these systems without needing to hook each individual system themselves."
val vuWebsite: String = "https://modrinth.com/plugin/vaultunlocked"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://repo.codemc.io/repository/creatorfromhell/")
    maven("https://repo.extendedclip.com/releases/")
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    compileOnly(libs.bukkit) {
        exclude("com.google.code.gson", "gson")
        exclude("com.google.guava", "guava")
        exclude("commons-lang", "commons-lang")
        exclude("junit", "junit")
        exclude("org.yaml", "snakeyaml")
    }
    api(libs.vault.unlocked.api)
    implementation(libs.bstats.bukkit)
    compileOnly(libs.placeholder.api)
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["shadow"])

        pom {
            name = project.name
            description = project.description
            url = vuWebsite
            organization {
                name = "The New Economy"
                url = "https://tnemc.net"
            }
            licenses {
                license {
                    name = "GNU Lesser General Public License, Version 3 (LGPL-3.0)"
                    url = "https://github.com/TheNewEconomy/VaultUnlocked/blob/master/license.txt"
                }
            }
            developers {
                developer {
                    id = "creatorfromhell"
                    name = "Daniel \"creatorfromhell\" Vidmar"
                    email = "daniel.viddy@gmail.com"
                    url = "https://cfh.dev"
                    organization = "The New Economy"
                    organizationUrl = "https://tnemc.net"
                    timezone = "America/New_York"
                    roles.add("Developer")
                }
            }
            scm {
                connection = "scm:git:git://github.com/TheNewEconomy/VaultUnlocked.git"
                developerConnection = connection
                url = "https://github.com/TheNewEconomy/VaultUnlocked/"
            }
            issueManagement {
                system = "GitHub"
                url = "https://github.com/TheNewEconomy/VaultUnlocked/issues"
            }
        }
    }

    repositories {
        maven {
            name = "CodeMC"
            url = uri("https://repo.codemc.io/repository/creatorfromhell/")

            val mavenUsername = System.getenv("GRADLE_PROJECT_MAVEN_USERNAME")
            val mavenPassword = System.getenv("GRADLE_PROJECT_MAVEN_PASSWORD")
            if (mavenUsername != null && mavenPassword != null) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    relocate("org.bstats","net.milkbowl.vault.metrics")
    manifest {
        attributes(
            "paperweight-mappings-namespace" to "mojang"
        )
    }
}

tasks.jar {
    archiveClassifier.set("original")
}

// Equivalent to Maven's resource filtering.
tasks.processResources {
    from(sourceSets.main.get().resources)
    include("plugin.yml")

    expand(
        "version" to project.version,
        "name" to "Vault",
        "description" to description!!,
        "url" to vuWebsite
    )
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
