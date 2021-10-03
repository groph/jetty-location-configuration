plugins {
    java
}

group = "org.imperfect.example.jetty.config.location"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:2.5.5")
    //implementation("org.springframework:spring-context:5.3.10")
    implementation("org.eclipse.jetty:jetty-rewrite:11.0.6") {
        exclude(group = "org.slf4j", module = "*")
    }
    implementation("org.apache.httpcomponents:httpclient:4.3.6")
    //implementation("ch.qos.logback:logback-classic:1.2.6")
    ///implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
}
