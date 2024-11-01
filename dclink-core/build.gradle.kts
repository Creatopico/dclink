repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    implementation(project(":dclink-api"))
    implementation(libs.adventure.api)
    implementation(libs.adventure.minimessage)
    implementation(libs.cloud.core)
    implementation(libs.cloud.brigadier)
    implementation(libs.configurate.hocon)
    implementation(libs.jda) {
        exclude(module= "opus-java")
    }

    implementation(libs.postgresql)
    implementation(libs.jdbi3.core)
    implementation(libs.jdbi3.postgres)
    implementation(libs.jdbi3.sqlobject)

//    implementation("org.postgresql:postgresql:42.7.1")
//    implementation("org.jdbi:jdbi3-core:3.44.0")
//    implementation("org.jdbi:jdbi3-postgres:3.44.0")
//    implementation("org.jdbi:jdbi3-sqlobject:3.44.0")


    compileOnly(libs.floodgate.api)
}