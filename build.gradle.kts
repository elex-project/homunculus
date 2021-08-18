plugins {
	java
	`java-library`
	`maven-publish`
	id("com.github.ben-manes.versions") version "0.39.0"
}

group = "com.elex-project"
version = "1.0-SNAPSHOT"
description = ""//todo

repositories {
	maven {
		url = uri("https://repository.elex-project.com/repository/maven")
	}
	maven {
		url = uri("https://repo.maven.apache.org/maven2")
		name = "Maven Central"
	}
}

java {
	withSourcesJar()
	withJavadocJar()
	sourceCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
	targetCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
	//modularity.inferModulePath.set(false)
}

configurations {
	compileOnly {
		extendsFrom(annotationProcessor.get())
	}
	testCompileOnly {
		extendsFrom(testAnnotationProcessor.get())
	}
}

tasks.jar {
	manifest { // todo
		attributes(mapOf(
				"Implementation-Title" to project.name,
				"Implementation-Version" to project.version,
				"Implementation-Vendor" to "ELEX co.,pte.",
				"Automatic-Module-Name" to "com.elex_project.${project.name}"
		))
	}
}

tasks.compileJava {
	options.encoding = "UTF-8"
	options.javaModuleVersion.set(provider { project.version as String })
}

tasks.compileTestJava {
	options.encoding = "UTF-8"
}

tasks.test {
	useJUnitPlatform()
}

tasks.javadoc {
	if (JavaVersion.current().isJava9Compatible) {
		(options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
	}
	(options as StandardJavadocDocletOptions).encoding = "UTF-8"
	(options as StandardJavadocDocletOptions).charSet = "UTF-8"
	(options as StandardJavadocDocletOptions).docEncoding = "UTF-8"

}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			pom {
				// todo
				name.set(project.name)
				description.set(project.description)
				url.set("https://www.elex-project.com/")
				inceptionYear.set("2021")
				properties.set(mapOf(
						"myProp" to "value",
						"prop.with.dots" to "anotherValue"
				))
				organization {
					name.set("Elex co.,Pte.")
					url.set("https://www.elex-project.com/")
				}
				licenses {
					license {
						// todo
						name.set("Apache License 2.0")
						url.set("https://github.com/elex-project/${project.name}/blob/main/LICENSE")
						comments.set("")
					}
				}
				developers {
					developer {
						id.set("elex")
						name.set("Elex")
						url.set("https://www.elex.pe.kr/")
						email.set("developer@elex-project.com")
						organization.set("Elex Co.,Pte.")
						organizationUrl.set("https://www.elex-project.com/")
						roles.set(arrayListOf("Developer", "CEO"))
						timezone.set("Asia/Seoul")
						properties.set(mapOf("" to ""))
					}
				}
				contributors {
					contributor {
						name.set("")
						email.set("")
						url.set("")
					}
				}
				scm {
					// todo
					connection.set("scm:git:https://github.com/elex-project/${project.name}.git")
					developerConnection.set("scm:git:https://github.com/elex-project/${project.name}.git")
					url.set("https://github.com/elex-project/${project.name}/")
				}
			}
		}
	}

	repositories {
		maven {
			name = "mavenElex"
			val urlRelease = uri("https://repository.elex-project.com/repository/maven-releases")
			val urlSnapshot = uri("https://repository.elex-project.com/repository/maven-snapshots")
			url = if (version.toString().endsWith("SNAPSHOT")) urlSnapshot else urlRelease
			// Repository credential, Must be defined in ~/.gradle/gradle.properties
			credentials {
				username = project.findProperty("repo.username") as String
				password = project.findProperty("repo.password") as String
			}
		}
		maven { //todo
			name = "mavenGithub"
			url = uri("https://maven.pkg.github.com/elex-project/${project.name}")
			credentials {
				username = project.findProperty("github.username") as String
				password = project.findProperty("github.token") as String
			}
		}
	}
}

dependencies {
	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
	implementation("org.slf4j:slf4j-api:1.7.32")
	implementation("org.jetbrains:annotations:22.0.0")

	compileOnly("org.projectlombok:lombok:1.18.20")
	annotationProcessor("org.projectlombok:lombok:1.18.20")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.20")

	testImplementation("ch.qos.logback:logback-classic:1.2.5")
	testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}
