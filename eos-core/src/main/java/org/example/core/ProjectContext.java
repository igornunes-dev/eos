package org.example.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class ProjectContext {
    private final Path projectRoot;
    private final Path sourceRoot;
    private final String basePackage;

    private ProjectContext(Path projectRoot, Path sourceRoot, String basePackage) {
        this.projectRoot = projectRoot;
        this.sourceRoot = sourceRoot;
        this.basePackage = basePackage;
    }

    public Path getProjectRoot() {
        return projectRoot;
    }

    public Path getSourceRoot() {
        return sourceRoot;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public static ProjectContext analyze() throws IOException {
        Path currentDir = Paths.get(System.getProperty("user.dir"));

        Path projectRoot = findProjectRoot(currentDir)
                .orElseThrow(() -> new IOException("Not a Spring Boot project. No pom.xml or build.gradle found."));

        Path sourceRoot = projectRoot.resolve("src/main/java");
        if (!Files.exists(sourceRoot)) {
            throw new IOException("Source directory not found: " + sourceRoot);
        }

        String basePackage = detectBasePackage(sourceRoot)
                .orElseThrow(() -> new IOException("Could not detect base package"));

        return new ProjectContext(projectRoot, sourceRoot, basePackage);
    }

    private static Optional<Path> findProjectRoot(Path start) {
        Path current = start;
        while (current != null) {
            if (Files.exists(current.resolve("pom.xml")) ||
                    Files.exists(current.resolve("build.gradle")) ||
                    Files.exists(current.resolve("build.gradle.kts"))) {
                return Optional.of(current);
            }
            current = current.getParent();
        }
        return Optional.empty();
    }

    private static Optional<String> detectBasePackage(Path sourceRoot) throws IOException {
        try (Stream<Path> paths = Files.walk(sourceRoot, 5)) {
            return paths
                    .filter(path -> path.toString().endsWith("Application.java"))
                    .findFirst()
                    .map(path -> {
                        Path relativePath = sourceRoot.relativize(path.getParent());
                        return relativePath.toString()
                                .replace(File.separator, ".");
                    });
        }
    }

    public Path getPackagePath(String subPackage) {
        String fullPackage = basePackage + "." + subPackage;
        String packagePath = fullPackage.replace(".", File.separator);
        return sourceRoot.resolve(packagePath);
    }

    public String getFullPackageName(String subPackage) {
        return basePackage + "." + subPackage;
    }

    /**
     * Detecta o nome correto do package (singular ou plural)
     * Ex: se existe "controllers", usa "controllers", senão usa "controller"
     */
    public String detectPackageName(String defaultName) {
        // Tenta plural primeiro
        String plural = defaultName.endsWith("s") ? defaultName : defaultName + "s";
        Path pluralPath = getPackagePath(plural);

        if (Files.exists(pluralPath) && Files.isDirectory(pluralPath)) {
            return plural;
        }

        // Tenta singular
        Path singularPath = getPackagePath(defaultName);
        if (Files.exists(singularPath) && Files.isDirectory(singularPath)) {
            return defaultName;
        }

        // Se nenhum existe, retorna o padrão
        return defaultName;
    }
}