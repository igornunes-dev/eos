package org.example.maven;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ParserConfiguration;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.example.core.ProjectContext;
import org.example.generator.GenerationParams;
import org.example.generator.GenerationResult;
import org.example.generator.impl.FactoryGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Mojo(name = "factory", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class FactoryMojo extends AbstractEosMojo {

    @Override
    public void execute() throws MojoExecutionException {
        try {
            // Configura o JavaParser para suportar Records e features modernas do Java
            StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);

            getLog().info("Generating Factory: " + name);

            ProjectContext context = getProjectContext();
            FactoryGenerator generator = new FactoryGenerator();

            String detectedPackage = packageName != null
                    ? packageName
                    : "factories";

            getLog().info("Using package: " + detectedPackage);

            String entityName = name.endsWith("Factory")
                    ? name.substring(0, name.length() - "Factory".length())
                    : name;

            String modelPackage = context.detectPackageName("model");
            Path modelBasePath = context.getPackagePath(modelPackage);
            Path entityPath = findEntityFile(modelBasePath, entityName);

            if (entityPath == null || !Files.exists(entityPath)) {
                throw new MojoExecutionException(
                        "Entity source file not found for: " + entityName +
                                " in package: " + modelPackage +
                                " (searched in: " + modelBasePath + ")"
                );
            }

            getLog().info("Found entity: " + entityPath);

            String realClassName = entityPath.getFileName().toString();
            realClassName = realClassName.substring(0, realClassName.length() - 5);

            CompilationUnit fileModel = StaticJavaParser.parse(entityPath);

            Set<String> imports = new HashSet<>();
            String fullEntityImport = context.getFullPackageName(modelPackage) + "." + realClassName;
            imports.add(fullEntityImport);

            List<Map<String, Object>> fields = new ArrayList<>();
            fileModel.findAll(FieldDeclaration.class).forEach(fd -> {
                fd.getVariables().forEach(var -> {
                    String fieldName = var.getNameAsString();

                    if (fieldName.equals("createdAt") ||
                            fieldName.equals("updatedAt") ||
                            fieldName.equals("id") ||
                            fieldName.equals("serialVersionUID")) {
                        return;
                    }

                    String type = fd.getElementType().asString();
                    String fullType = getFullTypeName(fd.getElementType(), fileModel);

                    Map<String, Object> fieldData = new HashMap<>();
                    fieldData.put("name", fieldName);
                    fieldData.put("type", type);
                    fieldData.put("fullType", fullType);
                    fieldData.put("defaultValue", getDefaultForType(type, fieldName, fullType));

                    fields.add(fieldData);
                    addImportsForType(imports, fullType);
                });
            });

            Map<String, Object> requestDTO = findDTO(context, entityName, realClassName, "Request");
            Map<String, Object> responseDTO = findDTO(context, entityName, realClassName, "Response");

            getLog().info("RequestDTO found: " + (requestDTO != null));
            getLog().info("ResponseDTO found: " + (responseDTO != null));

            if (requestDTO != null) {
                imports.add((String) requestDTO.get("fullImport"));
                getLog().info("RequestDTO: " + requestDTO.get("className") + " with " +
                        ((List<?>) requestDTO.get("constructorParams")).size() + " params");
            }
            if (responseDTO != null) {
                imports.add((String) responseDTO.get("fullImport"));
                getLog().info("ResponseDTO: " + responseDTO.get("className") + " with " +
                        ((List<?>) responseDTO.get("constructorParams")).size() + " params");
            }

            // Sempre adiciona UUID pois usamos no setId
            imports.add("java.util.UUID");
            imports.add("java.util.ArrayList");
            imports.add("java.util.List");

            Map<String, Object> customParams = new HashMap<>();
            customParams.put("fields", fields);
            customParams.put("imports", new ArrayList<>(imports));
            customParams.put("isTestResource", true);
            customParams.put("entityClassName", realClassName);
            customParams.put("requestDTO", requestDTO);
            customParams.put("responseDTO", responseDTO);

            GenerationParams params = GenerationParams.builder()
                    .className(entityName)
                    .packageName(detectedPackage)
                    .overwrite(force)
                    .customParams(customParams)
                    .build();

            GenerationResult result = generator.generate(context, params);

            if (result.isSuccess()) {
                getLog().info("✓ " + result.getMessage());
                getLog().info("  Created: " + result.getGeneratedFile());
            } else {
                throw new MojoExecutionException(result.getMessage());
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read entity source", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate Factory", e);
        }
    }


}