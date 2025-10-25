package org.example.commands.make;

import org.example.core.ProjectContext;
import org.example.generator.GenerationParams;
import org.example.generator.GenerationResult;
import org.example.generator.impl.ServiceGenerator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
        name = "service",
        description = "Generate a service class"
)
public class MakeServiceCommand implements Runnable {

    @Parameters(index = "0", description = "Service name (e.g., UserService)")
    private String name;

    @Option(names = {"-p", "--package"}, description = "Package name (default: service)")
    private String packageName = "service";

    @Option(names = {"-f", "--force"}, description = "Overwrite existing file")
    private boolean force = false;

    @Override
    public void run() {
        try {
            ProjectContext context = ProjectContext.analyze();
            ServiceGenerator generator = new ServiceGenerator();

            GenerationParams params = GenerationParams.builder()
                    .className(name)
                    .packageName(packageName)
                    .overwrite(force)
                    .build();

            GenerationResult result = generator.generate(context, params);

            if (result.isSuccess()) {
                System.out.println("✓ " + result.getMessage());
                System.out.println("  Created: " + result.getGeneratedFile());
            } else {
                System.err.println("✗ " + result.getMessage());
                System.exit(1);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}