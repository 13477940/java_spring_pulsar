plugins {
    id("java")
}

group = "dev.hackfun"
version = "0.9.9_231006"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // servlet api
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    // jdbc, connection pool
    // https://mvnrepository.com/artifact/org.apache.tomcat/tomcat-jdbc
    // https://mvnrepository.com/artifact/hikari-cp/hikari-cp
    implementation("org.apache.tomcat:tomcat-jdbc:10.1.13")
    implementation("com.mysql:mysql-connector-j:8.1.0")
    // bcprov
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    // gson
    implementation("com.google.code.gson:gson:2.10.1")
    // tika
    implementation("org.apache.tika:tika-core:2.9.0")
    implementation("commons-io:commons-io:2.13.0")
    implementation("org.slf4j:slf4j-api:2.0.9")
    // slf4j simple - for redis pooling use
    testImplementation("org.slf4j:slf4j-simple:2.0.9")
    // zxing
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.google.zxing:javase:3.5.2")
    // java cv
    implementation("org.bytedeco:javacv:1.5.9")
    // redis
    implementation("redis.clients:jedis:5.0.1")
    // hibernate
    implementation("org.hibernate:hibernate-core:5.6.1.Final")
    implementation("org.hibernate:hibernate-entitymanager:5.6.1.Final")
    implementation("org.hibernate.common:hibernate-commons-annotations:5.1.0.Final")
    implementation("javax.persistence:javax.persistence-api:2.2")
}

tasks.test {
    useJUnitPlatform()
}
