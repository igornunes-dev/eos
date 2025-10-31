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

    public String detectPackageName(String defaultName) {
        String plural;

        if (defaultName.endsWith("y")) {
            plural = defaultName.substring(0, defaultName.length() - 1) + "ies";
        } else if (defaultName.endsWith("s")) {
            plural = defaultName;
        } else {
            plural = defaultName + "s";
        }

        Path pluralPath = getPackagePath(plural);
        if (Files.exists(pluralPath) && Files.isDirectory(pluralPath)) {
            return plural;
        }

        Path singularPath = getPackagePath(defaultName);
        if (Files.exists(singularPath) && Files.isDirectory(singularPath)) {
            return defaultName;
        }

        return defaultName;
    }

    public boolean hasLombok() {
        try {
            Path pomPath = projectRoot.resolve("pom.xml");
            if (!Files.exists(pomPath)) {
                return false;
            }

            String pomContent = Files.readString(pomPath);
            return pomContent.contains("lombok") || pomContent.contains("org.projectlombok");

        } catch (IOException e) {
            return false;
        }
    }

    public boolean hasMapStruct() {
        try {
            Path pomPath = projectRoot.resolve("pom.xml");
            if (!Files.exists(pomPath)) {
                return false;
            }

            String pomContent = Files.readString(pomPath);
            return pomContent.contains("mapstruct") || pomContent.contains("org.mapstruct");

        } catch (IOException e) {
            return false;
        }
    }

    public Path findJavaFile(String className) throws IOException {
        Path srcPath = projectRoot.resolve("src/main/java");

        String targetFileName = className.endsWith(".java")
                ? className
                : className + ".java";

        try (Stream<Path> files = Files.walk(srcPath)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equals(targetFileName))
                    .findFirst()
                    .orElseThrow(() -> new IOException(
                            "Java file for class '" + className + "' not found under " + srcPath
                    ));
        }
    }

    public Path getTestPackagePath(String packageName) {
        Path testJavaPath = projectRoot
                .resolve("src")
                .resolve("test")
                .resolve("java");

        String[] packageParts = basePackage.split("\\.");
        Path fullPath = testJavaPath;
        for (String part : packageParts) {
            fullPath = fullPath.resolve(part);
        }

        if (packageName != null && !packageName.isEmpty()) {
            String[] subPackageParts = packageName.split("\\.");
            for (String part : subPackageParts) {
                fullPath = fullPath.resolve(part);
            }
        }

        return fullPath;
    }
}