package org.example.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.example.core.ProjectContext;
import org.example.generator.GenerationParams;
import org.example.generator.GenerationResult;
import org.example.generator.impl.EntityGenerator;

@Mojo(name = "entity", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class EntityMojo extends AbstractEosMojo {

    @Override
    public void execute() throws MojoExecutionException {
        try {
            getLog().info("Generating entity: " + name);

            ProjectContext context = getProjectContext();
            EntityGenerator generator = new EntityGenerator();

            String detectedPackage = packageName != null
                    ? packageName
                    : context.detectPackageName("model");

            getLog().info("Using package: " + detectedPackage);

            GenerationParams params = GenerationParams.builder()
                    .className(name)
                    .packageName(detectedPackage)
                    .overwrite(force)
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