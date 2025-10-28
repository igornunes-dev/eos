package org.example.maven;


import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.example.core.ProjectContext;
import org.example.generator.GenerationParams;
import org.example.generator.GenerationResult;
import org.example.generator.impl.DTOGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mojo(name = "dto", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class DTOMojo extends AbstractEosMojo {

    @Override
    public void execute() throws MojoExecutionException {
        try {
            getLog().info("Generating DTO: " + name);

            ProjectContext context = getProjectContext();
            DTOGenerator generator = new DTOGenerator();

            String detectedPackage = packageName != null
                    ? packageName
                    : context.detectPackageName("dto");

            getLog().info("Using package: " + detectedPackage);

            String modelPackage = context.detectPackageName("model");
            Path entityPath = context.getPackagePath(modelPackage).resolve(name + ".java");

            if (!Files.exists(entityPath)) {
                throw new MojoExecutionException("Entity source file not found: " + entityPath);
            }

            CompilationUnit fileModel = StaticJavaParser.parse(entityPath);

            List<Map<String, String>> fields = new ArrayList<>();
            fileModel.findAll(FieldDeclaration.class).forEach(fd -> {
                fd.getVariables().forEach(var -> {
                    if (var.getNameAsString().equals("createdAt") || var.getNameAsString().equals("updatedAt"))
                        return;

                    fields.add(Map.of(
                            "name", var.getNameAsString(),
                            "type", fd.getElementType().asString()
                    ));
                });
            });

            Map<String, Object> customParams = new HashMap<>();
            customParams.put("fields", fields);

            GenerationParams params = GenerationParams.builder()
                    .className(name + "DTO")
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
            throw new MojoExecutionException("Failed to generate DTO", e);
        }
    }
}