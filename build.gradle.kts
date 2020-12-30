import java.util.*

plugins {
    kotlin("jvm") version "1.4.21"
    java
    maven
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.1"
}

val artifactId = "gistcafe"
val repoName = "ServiceStack/gistcafe-kotlin"
val repoUrl = "https://github.com/$repoName"
val orgName = "ServiceStack"
val shortDesc = "gist.cafe utils for Kotlin"
val artifactVersion = version.toString()

val bintrayUpload: com.jfrog.bintray.gradle.tasks.BintrayUploadTask by tasks
val clean: Task by tasks
val build: Task by tasks

repositories {
    jcenter()
}

tasks {
    create<Jar>("sourceJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    create<Jar>("javadocJar") {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }
    withType<Jar> {
        archiveBaseName.set(artifactId)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourceJar"])
            artifactId = artifactId
            version = artifactVersion
            pom {
                name.set(artifactId)
                description.set(shortDesc)
                url.set("https://github.com/$repoName")
                licenses {
                    license {
                        name.set("The 3-Clause BSD License")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                        distribution.set("repo")
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

bintrayUpload.apply {
    dependsOn(clean)
    dependsOn(build)
    build.mustRunAfter(clean)

    onlyIf { System.getenv("BINTRAY_USER").isNotEmpty() }
    onlyIf { System.getenv("BINTRAY_APIKEY").isNotEmpty() }

    doFirst {
        println("Generating Maven POM file to $buildDir/poms/pom-default.xml")
        maven.pom {
            project {
                withGroovyBuilder {
                    "name"(artifactId)
                    "description"(shortDesc)
                    "url"("https://github.com/$repoName")
                    "licenses" {
                        "license" {
                            "name"("The 3-Clause BSD License")
                            "url"("https://opensource.org/licenses/BSD-3-Clause")
                            "distribution"("repo")
                        }
                    }
                    "developers" {
                        "developer" {
                            "id"(orgName)
                            "name"("$orgName, Inc.")
                            "email"("team@servicestack.net")
                        }
                    }
                    "scm" {
                        "connection"("scm:git:git://github.com/$repoName.git")
                        "developerConnection"("scm:git:ssh://github.com/$repoName.git")
                        "url"(repoUrl)
                    }
                }
            }
        }
        .writeTo("$buildDir/poms/pom-default.xml")
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_APIKEY")
    publish = true
    setConfigurations("archives")

    pkg.apply {
        repo = "maven"
        name =  artifactId
        userOrg = orgName.toLowerCase()

        websiteUrl = "https://gist.cafe"
        issueTrackerUrl = "https://github.com/$repoName/issues"
        githubRepo = repoName
        vcsUrl = "https://github.com/$repoName"
        description = shortDesc
        setLabels("servicestack","gistcafe","kotlin")
        setLicenses("BSD 3-Clause")
        desc = shortDesc

        version.apply {
            name = artifactVersion
            desc = repoUrl
            released = Date().toString()
            vcsTag = artifactVersion

            gpg.apply {
                sign = true
            }

            mavenCentralSync.apply {
                sync = true
                user = System.getenv("OSSRH_USER")
                password = System.getenv("OSSRH_PASS")
                close = "1"
            }
        }
    }
}

artifacts {
    add("archives", tasks["jar"])
    add("archives", tasks["sourceJar"])
    add("archives", tasks["javadocJar"])
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
