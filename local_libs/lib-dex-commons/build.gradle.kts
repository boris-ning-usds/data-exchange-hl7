import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    `java-library`
    `maven-publish`
    jacoco
}

group = "gov.cdc.dex"
version = "1.0.12-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.azure:azure-messaging-eventhubs:5.14.0")
    implementation("redis.clients:jedis:4.3.1")

    testImplementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.18.0")
}

tasks.test {
    useJUnitPlatform()
    environment (mapOf("REDIS_CACHE_NAME" to "ocio-ede-dev-dex-cache.redis.cache.windows.net",
                       "REDIS_CACHE_KEY"  to findProperty("redisDevKey")
    ))
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}


publishing {
    publications {
        register<MavenPublication>("lib-dex-commons") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/boris-ning-usds/data-exchange-hl7")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

