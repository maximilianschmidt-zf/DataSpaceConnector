/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       ZF Friedrichshafen AG - Initial API and Implementation
 *
 */

plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}


dependencies {
    api(project(":spi"))
    api(project(":extensions:iam:ssi:ssi-identity-service"))


    implementation(project(":core"))
    implementation(project(":extensions:api:data-management"))
    implementation(project(":extensions:filesystem:configuration-fs"))
    implementation("info.weboftrust:ld-signatures-java:1.0.0")
    implementation("decentralized-identity:jsonld-common-java:1.0.0")
    implementation("com.github.multiformats:java-multibase:v1.1.0")
}

repositories {
    maven {
        url = uri("https://repo.danubetech.com/repository/maven-public/")
    }
    maven {
        url = uri("https://jitpack.io/")
    }
}

application {
    mainClass.set("org.eclipse.dataspaceconnector.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("miw.jar")
}

publishing {
    publications {
        create<MavenPublication>("ssi") {
            artifactId = "ssi"
            from(components["java"])
        }
    }
}
