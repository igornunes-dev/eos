package org.example.generator.impl;

import org.example.core.ProjectContext;
import org.example.generator.CodeGenerate;
import org.example.generator.GenerationParams;
import org.example.generator.GenerationResult;
import org.example.templates.TemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RepositoryGenerator implements CodeGenerate {
    private final TemplateEngine templateEngine;

    public RepositoryGenerator() {
        this.templateEngine = new TemplateEngine();
    }

    @Override
    public GenerationResult generate(ProjectContext context, GenerationParams params) throws IOException {
        String className = params.getClassName();
        String packageName = params.getPackageName() != null
                ? params.getPackageName()
                : "repository";

        String idType = (String) params.getCustomParam("idType");
        boolean hasCustomIdType = idType != null && !idType.isBlank();
        if (!hasCustomIdType) {
            idType = "Long";
        }

        String modelPackage = context.detectPackageName("model");

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("packageName", context.getFullPackageName(packageName));
        templateData.put("entityPackage", context.getFullPackageName(modelPackage));
        templateData.put("className", className);
        templateData.put("idType", idType);
        templateData.put("hasCustomIdType", hasCustomIdType);

        String content = templateEngine.process("repository.ftl", templateData);

        Path packagePath = context.getPackagePath(packageName);
        Files.createDirectories(packagePath);

        Path filePath = packagePath.resolve(className + "Repository.java");

        if (fileExists(filePath) && !params.isOverwrite()) {
            return GenerationResult.error("File already exists: " + filePath);
        }

        Files.writeString(filePath, content);

        return GenerationResult.success(filePath);
    }

}
