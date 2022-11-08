import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

repositories {
    maven {
        name = "placeholderapi"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        url = uri("https://repo.clojars.org/")
    }
}

dependencies {
    implementation(project(":api"))
    implementation("org.zeroturnaround:zt-zip:1.15")
    implementation("com.github.puregero:multilib:1.1.8")
    compileOnly("me.clip:placeholderapi:2.11.2")
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set(null as String?)
        archiveFileName.set("CombatLogX.jar")

        relocate("org.zeroturnaround.zip", "com.github.sirblobman.combatlogx.zip")
        relocate("org.slf4j", "com.github.sirblobman.combatlogx.zip.slf4j")
        relocate("com.github.puregero.multilib", "com.github.sirblobman.combatlogx.multilib")
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        val calculatedVersion = rootProject.ext.get("calculatedVersion") as String
        val pluginName = (findProperty("bukkit.plugin.name") ?: "") as String
        val pluginPrefix = (findProperty("bukkit.plugin.prefix") ?: "") as String
        val pluginDescription = (findProperty("bukkit.plugin.description") ?: "") as String
        val pluginWebsite = (findProperty("bukkit.plugin.website") ?: "") as String
        val pluginMainClass = (findProperty("bukkit.plugin.main") ?: "") as String

        filesMatching("plugin.yml") {
            filter {
                it.replace("\${bukkit.plugin.name}", pluginName)
                    .replace("\${bukkit.plugin.prefix}", pluginPrefix)
                    .replace("\${bukkit.plugin.description}", pluginDescription)
                    .replace("\${bukkit.plugin.website}", pluginWebsite)
                    .replace("\${bukkit.plugin.main}", pluginMainClass)
                    .replace("\${bukkit.plugin.version}", calculatedVersion)
            }
        }

        filesMatching("config.yml") {
            filter {
                it.replace("\${bukkit.plugin.version}", calculatedVersion)
            }
        }
    }
}
