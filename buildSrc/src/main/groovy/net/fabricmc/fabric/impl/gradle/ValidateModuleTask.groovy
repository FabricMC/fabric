package net.fabricmc.fabric.impl.gradle

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class ValidateModuleTask extends DefaultTask {
    @TaskAction
    void validate() {
        def json = new JsonSlurper().parse(project.file("src/main/resources/fabric.mod.json") as File) as Map<String, Map<String, String>>;

        if (json.custom == null) {
            throw new GradleException("Module ${project} does not have a custom value containing module lifecycle!")
        }

        if (json.custom.get("fabric-api:module-lifecycle") == null) {
            throw new GradleException("Module ${project} does not have module lifecycle in custom values!")
        }

        // Validate the lifecycle value
        switch (json.custom.get("fabric-api:module-lifecycle")) {
            case "stable":
            case "experimental":
            case "deprecated":
                break;
            default:
                throw new GradleException("Module ${project} has an invalid module lifecycle ${json.custom.get('fabric-api:module-lifecycle')}");
        }
    }
}
