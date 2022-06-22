plugins {
    `maven-publish`
    `java-library`
    signing
}
val nimbusVersion: String by project
val rsApi: String by project

// `java-library`
dependencies {
    api(project(":core"))
    api(project(":extensions:http"))
    api(project(":spi"))
    api("com.nimbusds:nimbus-jose-jwt:${nimbusVersion}")

    implementation(project(":extensions:api:api-core"))
    implementation(project(":extensions:api:data-management:api-configuration"))
    //implementation(project(":extensions:iam:ssi:ssi-managed-identity-wallet-configuration"))
    implementation(project(":extensions:filesystem:configuration-fs"))
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
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


//publishing {
//    publications {
//        create<MavenPublication>("mavenJava") {
//            artifactId = "ssi-managed-identity-wallet"
//            from(components["java"])
//        }
//    }
//}

//signing {
//    sign(publishing.publications["mavenJava"])
//}
