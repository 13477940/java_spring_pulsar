plugins {
	java
}

group = "dev.hackfun"
version = "0.9.7_230922"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
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
	// zxing
	implementation("com.google.zxing:core:3.5.2")
	implementation("com.google.zxing:javase:3.5.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
