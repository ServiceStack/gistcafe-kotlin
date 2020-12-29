plugins {
    kotlin("jvm") version "1.4.21"
    java
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.0"
}

val artifactId = "gistcafe"
val repoName = "ServiceStack/gistcafe-kotlin"
val repoUrl = "https://github.com/$repoName"
val orgName = "ServiceStack"
val shortDesc = "gist.cafe utils for Kotlin"
val artifactVersion = version.toString()

repositories {
    jcenter()
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

publishing {
    publications {
        register("gpr", MavenPublication::class) {
            from(components["java"])

            artifact(sourcesJar)

            pom {
                name.set(artifactId)
                description.set(shortDesc)
                url.set("https://github.com/$repoName")
                licenses {
                    license {
                        name.set("The 3-Clause BSD License")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                    }
                }
                developers {
                    developer {
                        id.set(orgName)
                        name.set("$orgName, Inc.")
                        email.set("team@servicestack.net")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/$repoName.git")
                    developerConnection.set("scm:git:ssh://github.com/$repoName.git")
                    url.set(repoUrl)
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/servicestack/gistcafe-kotlin")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_APIKEY")
    publish = true
    setPublications(artifactId)
    pkg.apply {
        repo = "maven"
        name =  artifactId
        userOrg = orgName
        websiteUrl = "https://gist.cafe"
        githubRepo = repoName
        vcsUrl = "https://github.com/$repoName"
        description = shortDesc
        setLabels("kotlin")
        setLicenses("BSD")
        desc = shortDesc

        version.apply {
            name = artifactVersion
            desc = repoUrl
            vcsTag = artifactVersion
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.8.6")
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
