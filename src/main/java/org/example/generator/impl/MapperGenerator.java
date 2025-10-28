package org.example.generator.impl;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
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

public class MapperGenerator implements CodeGenerate {

    private final TemplateEngine templateEngine;

    public MapperGenerator() {
        this.templateEngine = new TemplateEngine();
        StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
    }

    @Override
    public GenerationResult generate(ProjectContext context, GenerationParams params) throws IOException {
        String className = params.getClassName();
        String packageName = params.getPackageName() != null ? params.getPackageName() : "mappers";
        boolean hasMapStruct = context.hasMapStruct();

        String entityClassName = (String) params.getCustomParam("entityClassName");
        String dtoClassName = (String) params.getCustomParam("dtoClassName");

        String entityPackage = resolvePackage(context, entityClassName, "models");
        String dtoPackage = resolvePackage(context, dtoClassName, "dtos");

        entityClassName = entityPackage + "." + simpleName(entityClassName);
        dtoClassName = dtoPackage + "." + simpleName(dtoClassName);

        String mapperClassName = className.endsWith("Mapper") ? className : className + "Mapper";

        String entitySimpleName = entityClassName.contains(".")
                ? entityClassName.substring(entityClassName.lastIndexOf('.') + 1)
                : entityClassName;

        String dtoSimpleName = dtoClassName.contains(".")
                ? dtoClassName.substring(dtoClassName.lastIndexOf('.') + 1)
                : dtoClassName;

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("packageName", context.getFullPackageName(packageName));
        templateData.put("className", mapperClassName);
        templateData.put("entitySimpleName", entitySimpleName);
        templateData.put("dtoSimpleName", dtoSimpleName);
        templateData.put("hasMapStruct", hasMapStruct);
        templateData.put("entityClassName", entityClassName);
        templateData.put("dtoClassName", dtoClassName);
        templateData.put("entityPackage", entityPackage);
        templateData.put("dtoPackage", dtoPackage);

        // Gera o arquivo final
        String content = templateEngine.process("mapper.ftl", templateData);
        Path packagePath = context.getPackagePath(packageName);
        Files.createDirectories(packagePath);

        Path filePath = packagePath.resolve(className + "Mapper.java");
        if (fileExists(filePath) && !params.isOverwrite()) {
            return GenerationResult.error("File already exists: " + filePath);
        }

        Files.writeString(filePath, content);
        return GenerationResult.success(filePath);
    }

    private String resolvePackage(ProjectContext context, String className, String defaultPackage) throws IOException {
        if (className == null) return defaultPackage;

        // Se já for totalmente qualificado
        if (className.contains(".")) {
            int lastDot = className.lastIndexOf('.');
            return className.substring(0, lastDot);
        }

        // Localiza arquivo no projeto
        Path path = context.findJavaFile(className);
        CompilationUnit cu = StaticJavaParser.parse(path.toFile());
        return cu.getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse(defaultPackage);
    }

    private String simpleName(String className) {
        if (className == null) return null;
        int lastDot = className.lastIndexOf('.');
        return lastDot != -1 ? className.substring(lastDot + 1) : className;
    }
}
