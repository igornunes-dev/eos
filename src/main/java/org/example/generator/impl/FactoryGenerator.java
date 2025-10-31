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

public class FactoryGenerator implements CodeGenerate {
    private final TemplateEngine templateEngine;

    public FactoryGenerator() {
        this.templateEngine = new TemplateEngine();
    }

    @Override
    public GenerationResult generate(ProjectContext context, GenerationParams params) throws IOException {
        String className = params.getClassName();
        String packageName = params.getPackageName() != null
                ? params.getPackageName()
                : "factories";

        boolean isTestResource = Boolean.TRUE.equals(params.getCustomParam("isTestResource"));

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("package", context.getFullPackageName(packageName));
        templateData.put("className", className);
        templateData.put("entityClassName", params.getCustomParam("entityClassName"));
        templateData.put("fields", params.getCustomParam("fields"));
        templateData.put("imports", params.getCustomParam("imports"));
        templateData.put("requestDTO", params.getCustomParam("requestDTO"));
        templateData.put("responseDTO", params.getCustomParam("responseDTO"));

        String content = templateEngine.process("factory.ftl", templateData);

        Path basePath = isTestResource
                ? context.getTestPackagePath(packageName)
                : context.getPackagePath(packageName);

        Files.createDirectories(basePath);

        Path filePath = basePath.resolve(className + "Factory.java");

        if (Files.exists(filePath) && !params.isOverwrite()) {
            return GenerationResult.error("File already exists: " + filePath);
        }

        Files.writeString(filePath, content);

        return GenerationResult.success(filePath);
    }
}