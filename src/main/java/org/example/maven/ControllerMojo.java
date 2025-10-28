package org.example.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.example.core.ProjectContext;
import org.example.generator.GenerationParams;
import org.example.generator.GenerationResult;
import org.example.generator.impl.ControllerGenerator;

@Mojo(name = "controller", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ControllerMojo extends AbstractEosMojo {

    @Override
    public void execute() throws MojoExecutionException {
        try {
            getLog().info("Generating controller: " + name);

            ProjectContext context = getProjectContext();
            ControllerGenerator generator = new ControllerGenerator();

            String detectedPackage = packageName != null
                    ? packageName
                    : context.detectPackageName("controller");

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
            throw new MojoExecutionException("Failed to generate controller", e);
        }
    }
}