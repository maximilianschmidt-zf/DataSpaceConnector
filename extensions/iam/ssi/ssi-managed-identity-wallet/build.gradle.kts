plugins {
    `java-library`
}
val nimbusVersion: String by project
val rsApi: String by project

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
}

publishing {
    publications {
        create<MavenPublication>("ssi-managed-identity-wallet") {
            artifactId = "ssi-managed-identity-wallet"
            from(components["java"])
        }
    }
}
