package com.original.generator.core.domain.enums;

import java.io.File;
import java.util.Arrays;
import java.util.function.BiFunction;

public enum TemplateType {
    BACKEND_JAVA(".java", (projectPath, packagePath) ->
            String.format("%s/src/main/java/%s", projectPath, packagePath.replace(".", File.separator))),
    BACKEND_RESOURCE(".xml", (projectPath, packagePath) ->
            String.format("%s/src/main/resources", projectPath)),
    FRONTEND_VUE(".vue", (projectPath, packagePath) ->
            String.format("%s/src/views", projectPath)),
    FRONTEND_JS(".js", (projectPath, packagePath) ->
            String.format("%s/src/api", projectPath)),
    FRONTEND_JSON(".json", (projectPath, packagePath) ->
            String.format("%s/src/config", projectPath));

    private final String suffix;
    private final BiFunction<String, String, String> pathGenerator;

    TemplateType(String suffix, BiFunction<String, String, String> pathGenerator) {
        this.suffix = suffix;
        this.pathGenerator = pathGenerator;
    }

    public String generatePath(String projectPath, String packagePath) {
        return pathGenerator.apply(projectPath, packagePath);
    }

    public static TemplateType fromFileName(String fileName) {
        return Arrays.stream(values())
                .filter(type -> fileName.endsWith(type.suffix))
                .findFirst()
                .orElse(BACKEND_JAVA);
    }
} 