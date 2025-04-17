

plugins {
    kotlin("jvm") version "2.2.0-Beta1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

tasks.named<Jar>("jar") {
    // Otherwise you'll get a "No main manifest attribute" error
    manifest {
        attributes["Main-Class"] = "tortel.boatHider.Main"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    destinationDirectory.set(file("D:/minecrafservers/paper1.21.4/plugins"))
}

group = "tortel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.11")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.11")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.20")
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
    }

    build {
        dependsOn("shadowJar")
    }

    processResources{
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

//tasks.withType<KotlinJvmCompile> {
//    compilerOptions {
//        javaParameters = true
//    }
//}