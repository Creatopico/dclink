plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.velocity.run)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://maven.elytrium.net/repo/")
}

dependencies {
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)
    implementation(libs.adventure.minimessage)
    implementation(libs.cloud.velocity)
    implementation(project(":dclink-api"))
    implementation(project(":dclink-core"))
    compileOnly("net.elytrium.limboapi:api:1.1.16")
    compileOnly("com.velocitypowered:velocity-proxy:3.2.0-SNAPSHOT") // From Elytrium Repo.
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.api.get().toString())
    }

//    shadowJar {
//        archiveClassifier.set("")
//
//        fun reloc(pkg: String) = relocate(pkg, "com.kalimero2.team.dclink.libs.$pkg")
//        reloc("cloud.commandframework")
//        reloc("io.leangen")
//        reloc("net.dv8tion")
//        reloc("org.xerial")
//        reloc("org.jdbi")
//        reloc("org.postgresql")
//    }

    processResources {
        filesMatching("velocity-plugin.json"){
            expand(
                "version" to project.version,
            )
        }
    }
}