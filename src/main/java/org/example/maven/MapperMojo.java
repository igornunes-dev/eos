package org.example.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.example.core.ProjectContext;
import org.example.generator.GenerationParams;
import org.example.generator.GenerationResult;
import org.example.generator.impl.MapperGenerator;

import java.util.HashMap;
import java.util.Map;

@Mojo(name = "mapper", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MapperMojo extends AbstractEosMojo {

    @Parameter(property = "entity", required = true)
    private String entityClassName;

    @Parameter(property = "dto", required = true)
    private String dtoClassName;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            getLog().info("Generating mapper: " + name);

            ProjectContext context = getProjectContext();
            MapperGenerator generator = new MapperGenerator();

            String detectedPackage = packageName != null
                    ? packageName
                    : context.detectPackageName("mapper");

            getLog().info("Using package: " + detectedPackage);

            Map<String, Object> customParams = new HashMap<>();
            customParams.put("entityClassName", entityClassName);
            customParams.put("dtoClassName", dtoClassName);

            GenerationParams params = GenerationParams.builder()
                    .className(name)
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

        } catch (MojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate mapper", e);
        }
    }
}
