package org.example.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.example.core.ProjectContext;
import org.example.generator.GenerationParams;
import org.example.generator.GenerationResult;
import org.example.generator.impl.ServiceGenerator;

@Mojo(name = "service", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ServiceMojo extends AbstractEosMojo {

    @Override
    public void execute() throws MojoExecutionException {
        try {
            ProjectContext context = getProjectContext();
            ServiceGenerator generator = new ServiceGenerator();

            String detectedPackage = packageName != null
                    ? packageName
                    : context.detectPackageName("service");

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
            throw new MojoExecutionException("Failed to generate service", e);
        }
    }
}