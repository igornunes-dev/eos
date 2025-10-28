package org.example.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.example.core.ProjectContext;
import org.example.generator.GenerationParams;
import org.example.generator.GenerationResult;
import org.example.generator.impl.RepositoryGenerator;

import java.util.HashMap;
import java.util.Map;

@Mojo(name = "repository", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RepositoryMojo extends AbstractEosMojo {
    @Override
    public void execute() throws MojoExecutionException {
        try {
            getLog().info("Generating entity: " + name);

            ProjectContext context = getProjectContext();
            RepositoryGenerator generator = new RepositoryGenerator();

            String detectedPackage = packageName != null
                    ? packageName
                    : context.detectPackageName("repository");

            Map<String, Object> customParams = new HashMap<>();
            customParams.put("idType", idType);

            getLog().info("Using package: " + detectedPackage);

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
            throw new MojoExecutionException("Failed to generate entity", e);
        }
    }
}
