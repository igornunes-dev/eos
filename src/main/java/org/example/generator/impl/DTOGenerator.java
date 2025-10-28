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
import java.util.List;
import java.util.Map;

public class DTOGenerator implements CodeGenerate {

    private final TemplateEngine templateEngine;

    public DTOGenerator() {
        this.templateEngine = new TemplateEngine();
    }

    @Override
    public GenerationResult generate(ProjectContext context, GenerationParams params) throws IOException {
        String dtoClassName = params.getClassName();
        String packageName = params.getPackageName() != null
                ? params.getPackageName()
                : "dto";

        List<Map<String, String>> fieldsList = (List<Map<String, String>>) params.getCustomParam("fields");
        if (fieldsList == null || fieldsList.isEmpty()) {
            return GenerationResult.error("No fields provided to generate DTO");
        }

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("packageName", context.getFullPackageName(packageName));
        templateData.put("className", dtoClassName);
        templateData.put("fields", fieldsList);

        String content = templateEngine.process("dto.ftl", templateData);

        Path packagePath = context.getPackagePath(packageName);
        Files.createDirectories(packagePath);

        Path filePath = packagePath.resolve(dtoClassName + ".java");

        if (fileExists(filePath) && !params.isOverwrite()) {
            return GenerationResult.error("File already exists: " + filePath);
        }

        Files.writeString(filePath, content);

        return GenerationResult.success(filePath);
    }
}

