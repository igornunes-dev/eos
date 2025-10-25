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

public class ServiceGenerator implements CodeGenerate {
    private final TemplateEngine templateEngine;

    public ServiceGenerator() {
        this.templateEngine = new TemplateEngine();
    }

    @Override
    public GenerationResult generate(ProjectContext context, GenerationParams params) throws IOException {
        String className = params.getClassName();
        String packageName = params.getPackageName() != null
                ? params.getPackageName()
                : "service";

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("package", context.getFullPackageName(packageName));
        templateData.put("className", className);
        templateData.put("entityName", className.replace("Service", ""));

        String content = templateEngine.process("service.ftl", templateData);

        Path packagePath = context.getPackagePath(packageName);
        Files.createDirectories(packagePath);

        Path filePath = packagePath.resolve(className + ".java");

        if (fileExists(filePath) && !params.isOverwrite()) {
            return GenerationResult.error("File already exists: " + filePath);
        }

        Files.writeString(filePath, content);

        return GenerationResult.success(filePath);
    }
}