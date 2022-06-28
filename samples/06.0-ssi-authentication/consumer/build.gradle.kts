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
 *       Microsoft Corporation - initial API and implementation
 *       Fraunhofer Institute for Software and Systems Engineering - added dependencies
 *       ZF Friedrichshafen AG - add dependency
 *
 */

plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val jupiterVersion: String by project
val rsApi: String by project

dependencies {
    //api(project(":data-protocols:ids:ids-core"))
    implementation(project(":core"))

    implementation(project(":extensions:filesystem:configuration-fs"))

    implementation(project(":extensions:iam:ssi:ssi-identity-service"))

    implementation(project(":extensions:api:auth-tokenbased"))
    implementation(project(":extensions:api:data-management"))

    implementation(project(":data-protocols:ids")){
        exclude("org.eclipse.dataspaceconnector","ids-token-validation")
    }
//    implementation(project(":data-protocols:ids:ids-api-configuration"))
//    implementation(project(":data-protocols:ids:ids-api-multipart-dispatcher-v1"))
//    implementation(project(":data-protocols:ids:ids-api-multipart-endpoint-v1"))
//    //api(project(":data-protocols:ids:ids-api-transform-v1"))
//    implementation(project(":data-protocols:ids:ids-transform-v1"))
//    implementation(project(":data-protocols:ids:ids-core"))
//    implementation(project(":data-protocols:ids:ids-spi"))


    //implementation(project(":spi:iam"))

//    implementation(project(":data-protocols:ids")) {
//        exclude("org.eclipse.dataspaceconnector","ids-token-validation")
//    }
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
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("consumer.jar")
}
