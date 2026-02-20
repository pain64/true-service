plugins {
  id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  mavenLocal()
}

java {
  sourceCompatibility = JavaVersion.VERSION_25
  targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<JavaCompile> {
  options.compilerArgs.add("-parameters")
  //options.compilerArgs.add("--add-opens java.base/java.lang=ALL-UNNAMED")
}

dependencies {
  compileOnly("org.projectlombok:lombok:1.18.42")
  annotationProcessor("org.projectlombok:lombok:1.18.42")
  implementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.13")
  implementation("tools.jackson.core:jackson-databind:3.0.1")

  testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  implementation("org.jetbrains:annotations:26.0.2-1")
  implementation("org.jspecify:jspecify:1.0.0")

  implementation("io.undertow:undertow-core:2.3.20.Final")
  implementation("io.netty:netty-all:4.2.9.Final")
  implementation("io.netty:netty-codec-http:4.2.9.Final")
  implementation("io.netty:netty-transport-native-io_uring:4.2.9.Final")
  implementation("io.netty:netty-transport-native-unix-common:4.2.9.Final")
  implementation("org.jctools:jctools-core:4.0.5")


  implementation("org.postgresql:postgresql:42.7.9")
  implementation("com.zaxxer:HikariCP:5.1.0")

}

tasks.test {
  useJUnitPlatform()
}