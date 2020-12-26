package net.fabricmc.fabric.impl.gradle

import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSet

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class ApiModuleExtension {
	private static final String FMJ_LOCATION = "src/%s/resources/fabric.mod.json";
	ModuleLifecycle lifecycle;

	void setup(Project project) {
		def mainFmj = project.file(String.format(FMJ_LOCATION, "main"))

		// Main source set
		if (!mainFmj.exists()) {
			project.logger.warn(String.format("Project %s does not have a \"fabric.mod.json\" at %s", project.name, mainFmj))
			return
		} else {
			def json = new JsonSlurper().parse(mainFmj) as Map<String, Object>
			configureForMain(project, project.sourceSets.getByName("main"), json)
		}

		def testmodFmj = project.file(String.format(FMJ_LOCATION, "testmod"))

		// Testmod is optional
		if (testmodFmj.exists()) {
			def json = new JsonSlurper().parse(testmodFmj) as Map<String, Object>
			configure(project, project.sourceSets.getByName("testmod"), json)
		}
	}

	private void configureForMain(Project project, SourceSet sourceSet, Map<String, Object> json) {
		// Dependencies
		def rawDeps = json.depends as Map<String, String>
		rawDeps.remove("fabricloader")
		rawDeps.remove("minecraft")
		def dependencies = rawDeps.keySet().iterator().collect { project.dependencies.project(path: ":$it", configuration: "dev") }

		dependencies.each {
			project.logger.debug(String.format("Resolving dependency \"%s\" for project \"%s\"", it, project.name))
			project.dependencies.add(sourceSet.getTaskName(null, JavaPlugin.COMPILE_CONFIGURATION_NAME), it)
		}

		// Configure publishing to add dependencies to pom
		project.publishing {
			publications {
				mavenJava(MavenPublication) {
					pom.withXml {
						def depsNode = asNode().appendNode("dependencies")

						dependencies.each {
							def depNode = depsNode.appendNode("dependency")
							depNode.appendNode("groupId", it.group)
							depNode.appendNode("artifactId", it.name)
							depNode.appendNode("version", it.version)
							depNode.appendNode("scope", "compile")
						}
					}
				}
			}
		}

		// Derive archivesBaseName from mod id.
		project.archivesBaseName = json.id as String

		// Derive version from fmj
		project.version = getSubprojectVersion(project, json.version as String)

		// Only apply access widener if it is required in fmj
		if (json.accessWidener != null) {
			project.loom.accessWidener = project.file("src/main/resources/${project.archivesBaseName}.accesswidener")
		}

		// The main sourceSet determines a module's lifecycle.
		def customValues = json.custom as Map<String, Object>

		if (customValues == null) {
			throw new GradleException(String.format("Module %s does not have a module lifecycle!", project.name))
		}

		def lifecycle = customValues.get("fabric-api:lifecycle")

		if (lifecycle == null) {
			throw new GradleException(String.format("Module %s does not have a module lifecycle!", project.name))
		}

		this.lifecycle = Enum.valueOf(ModuleLifecycle, (lifecycle as String).toUpperCase(Locale.ROOT));
	}

	private void configure(Project project, SourceSet sourceSet, Map<String, Object> json) {
		// Dependencies
		def dependencies = json.depends as Map<String, String>
		dependencies.remove("fabricloader")
		dependencies.remove("minecraft")

		dependencies.keySet().iterator().collect { project.dependencies.project(path: ":$it", configuration: "dev") }.each {
			project.logger.debug(String.format("Resolving dependency \"%s\" for project \"%s\"", it, project.name))
			project.dependencies.add(sourceSet.getTaskName(null, JavaPlugin.COMPILE_CONFIGURATION_NAME), it)
		}
	}

	private def getSubprojectVersion(Project project, String version) {
		if (project.grgit == null) {
			return version + "+nogit"
		}

		def latestCommits = project.grgit.log(paths: [project.name], maxCommits: 1)

		if (latestCommits.isEmpty()) {
			return version + "+uncommited"
		}

		return version + "+" + latestCommits.get(0).id.substring(0, 8) + Integer.toHexString(ByteBuffer.wrap(MessageDigest.getInstance("SHA-256").digest(Versions.mcVersion.getBytes(StandardCharsets.UTF_8))).getInt(0)).substring(0, 2)
	}
}
