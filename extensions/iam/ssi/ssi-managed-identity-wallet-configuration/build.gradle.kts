plugins {
    `java-library`
}
val nimbusVersion: String by project
val rsApi: String by project

dependencies {
    api(project(":spi"))
    api("com.nimbusds:nimbus-jose-jwt:${nimbusVersion}")
    implementation(project(":extensions:api:data-management"))

    implementation(project(":extensions:filesystem:configuration-fs"))

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
}

publishing {
    publications {
        create<MavenPublication>("ssi-managed-identity-wallet-configuration") {
            artifactId = "ssi-managed-identity-wallet-configuration"
            from(components["java"])
        }
    }
}
