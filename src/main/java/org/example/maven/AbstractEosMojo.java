package org.example.maven;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.type.Type;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.example.core.ProjectContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class AbstractEosMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(property = "name", required = true)
    protected String name;

    @Parameter(property = "idType")
    protected String idType;

    @Parameter(property = "package")
    protected String packageName;

    @Parameter(property = "force", defaultValue = "false")
    protected boolean force;

    protected ProjectContext getProjectContext() throws MojoExecutionException {
        try {
            System.setProperty("user.dir", project.getBasedir().getAbsolutePath());
            return ProjectContext.analyze();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to analyze project: " + e.getMessage(), e);
        }
    }

    protected Path findEntityFile(Path basePath, String entityName) throws IOException {
        Path directPath = basePath.resolve(entityName + ".java");
        if (Files.exists(directPath)) {
            return directPath;
        }

        if (Files.exists(basePath) && Files.isDirectory(basePath)) {
            try (var stream = Files.walk(basePath, 1)) {
                return stream
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".java"))
                        .filter(p -> {
                            String fileName = p.getFileName().toString();
                            String fileNameWithoutExt = fileName.substring(0, fileName.length() - 5);

                            return fileNameWithoutExt.equalsIgnoreCase(entityName) ||
                                    fileNameWithoutExt.equalsIgnoreCase(getSingular(entityName)) ||
                                    fileNameWithoutExt.equalsIgnoreCase(getPlural(entityName));
                        })
                        .findFirst()
                        .orElse(null);
            }
        }

        return null;
    }

    protected String getPlural(String word) {
        if (word.endsWith("y") && word.length() > 1 && !isVowel(word.charAt(word.length() - 2))) {
            return word.substring(0, word.length() - 1) + "ies";
        } else if (word.endsWith("s") || word.endsWith("x") ||
                word.endsWith("z") || word.endsWith("ch") ||
                word.endsWith("sh")) {
            return word + "es";
        } else {
            return word + "s";
        }
    }

    protected String getSingular(String word) {
        if (word.endsWith("ies") && word.length() > 3) {
            return word.substring(0, word.length() - 3) + "y";
        } else if (word.endsWith("es") && word.length() > 2) {
            return word.substring(0, word.length() - 2);
        } else if (word.endsWith("s") && word.length() > 1) {
            return word.substring(0, word.length() - 1);
        } else {
            return word;
        }
    }

    protected boolean isVowel(char c) {
        return "aeiouAEIOU".indexOf(c) != -1;
    }

    protected String getDefaultForType(String type, String name, boolean withNumber) {
        if (withNumber) {
            return switch (type) {
                case "String" -> "\"" + capitalize(name) + " \" + number";
                case "int", "Integer" -> "number";
                case "long", "Long" -> "(long) number";
                case "double", "Double" -> "(double) number";
                case "boolean", "Boolean" -> "number % 2 == 0";
                case "Set", "HashSet" -> "new HashSet<>()";
                case "List", "ArrayList" -> "new ArrayList<>()";
                case "UUID" -> "UUID.nameUUIDFromBytes((\"" + name + "-\" + number).getBytes())";
                default -> "null";
            };
        } else {
            return switch (type) {
                case "String" -> "\"" + capitalize(name) + " 0\"";
                case "int", "Integer" -> "0";
                case "long", "Long" -> "0L";
                case "double", "Double" -> "0.0";
                case "boolean", "Boolean" -> "false";
                case "Set", "HashSet" -> "new HashSet<>()";
                case "List", "ArrayList" -> "new ArrayList<>()";
                case "UUID" -> "UUID.randomUUID()";
                default -> "null";
            };
        }
    }

    protected String capitalize(String str) {
        if (str == null || str.isBlank()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    protected Map<String, Object> findDTO(ProjectContext context, String entityName,
                                        String realClassName, String dtoType) {
        try {
            String dtoPackage = context.detectPackageName("dto");
            Path dtoBasePath = context.getPackagePath(dtoPackage);

            getLog().info("Looking for " + dtoType + " DTO in: " + dtoBasePath);

            List<String> possibleDirs = Arrays.asList(
                    entityName.toLowerCase(),
                    entityName.toLowerCase() + "s",
                    realClassName.toLowerCase(),
                    realClassName.toLowerCase() + "s"
            );

            Path entityDtoPath = null;
            for (String dir : possibleDirs) {
                Path testPath = dtoBasePath.resolve(dir);
                if (Files.exists(testPath)) {
                    entityDtoPath = testPath;
                    break;
                }
            }

            if (entityDtoPath == null) {
                getLog().warn("DTO directory not found for entity: " + entityName);
                return null;
            }

            getLog().info("Searching DTOs in: " + entityDtoPath);

            Path dtoFile = findDTOFile(entityDtoPath, dtoType);

            if (dtoFile == null) {
                getLog().warn(dtoType + " DTO file not found");
                return null;
            }

            getLog().info("Found DTO file: " + dtoFile);

            String dtoClassName = dtoFile.getFileName().toString();
            dtoClassName = dtoClassName.substring(0, dtoClassName.length() - 5);

            CompilationUnit dtoModel = StaticJavaParser.parse(dtoFile);

            List<Map<String, String>> constructorParams = new ArrayList<>();
            boolean isRecord = false;

            Optional<RecordDeclaration> recordDecl = dtoModel.findFirst(RecordDeclaration.class);

            if (recordDecl.isPresent()) {
                getLog().info("Found Record declaration: " + dtoClassName);
                isRecord = true;
                recordDecl.get().getParameters().forEach(param -> {
                    String paramType = param.getType().asString();
                    constructorParams.add(Map.of(
                            "name", param.getNameAsString(),
                            "type", paramType,
                            "fullType", getFullTypeName(param.getType(), dtoModel)
                    ));
                });
            } else {
                Optional<ClassOrInterfaceDeclaration> classDecl =
                        dtoModel.findFirst(ClassOrInterfaceDeclaration.class);

                if (classDecl.isPresent()) {
                    getLog().info("Found Class declaration: " + dtoClassName);

                    Optional<ConstructorDeclaration> mainConstructor =
                            classDecl.get().getConstructors().stream()
                                    .max(Comparator.comparingInt(c -> c.getParameters().size()));

                    if (mainConstructor.isPresent()) {
                        getLog().info("Found constructor with " +
                                mainConstructor.get().getParameters().size() + " parameters");
                        mainConstructor.get().getParameters().forEach(param -> {
                            String paramType = param.getType().asString();
                            constructorParams.add(Map.of(
                                    "name", param.getNameAsString(),
                                    "type", paramType,
                                    "fullType", getFullTypeName(param.getType(), dtoModel)
                            ));
                        });
                    }
                }
            }

            if (constructorParams.isEmpty()) {
                getLog().warn("No constructor/record parameters found in DTO: " + dtoClassName);
                return null;
            }

            getLog().info("Extracted " + constructorParams.size() + " parameters from " +
                    (isRecord ? "Record" : "Class"));

            String subPackage = entityDtoPath.getFileName().toString();
            String fullImport = context.getFullPackageName(dtoPackage) + "." + subPackage + "." + dtoClassName;

            Map<String, Object> result = new HashMap<>();
            result.put("className", dtoClassName);
            result.put("fullImport", fullImport);
            result.put("constructorParams", constructorParams);
            result.put("isRecord", isRecord);

            return result;

        } catch (Exception e) {
            getLog().error("Error finding " + dtoType + " DTO: " + e.getMessage());
            getLog().debug("Full stack trace:", e);
            return null;
        }
    }

    protected Path findDTOFile(Path basePath, String dtoType) throws IOException {
        if (!Files.exists(basePath) || !Files.isDirectory(basePath)) {
            return null;
        }

        try (var stream = Files.walk(basePath, 1)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> {
                        String fileName = p.getFileName().toString();
                        return fileName.contains(dtoType + "DTO") ||
                                fileName.contains(dtoType + "Dto");
                    })
                    .findFirst()
                    .orElse(null);
        }
    }

    protected String getFullTypeName(Type type, CompilationUnit cu) {
        String typeName = type.asString();

        if (typeName.contains(".")) {
            return typeName;
        }

        for (var importDecl : cu.getImports()) {
            String importName = importDecl.getNameAsString();
            String simpleType = typeName.contains("<") ?
                    typeName.substring(0, typeName.indexOf("<")) : typeName;

            if (importName.endsWith("." + simpleType)) {
                return typeName.contains("<") ?
                        importName + typeName.substring(typeName.indexOf("<")) : importName;
            }
        }

        return typeName;
    }

    protected void addImportsForType(Set<String> imports, String type) {
        if (type.contains("LocalDate")) {
            imports.add("java.time.LocalDate");
        }
        if (type.contains("LocalDateTime")) {
            imports.add("java.time.LocalDateTime");
        }
        if (type.contains("UUID")) {
            imports.add("java.util.UUID");
        }
        if (type.contains("List") || type.contains("ArrayList")) {
            imports.add("java.util.ArrayList");
            imports.add("java.util.List");
        }
        if (type.contains("Set") || type.contains("HashSet")) {
            imports.add("java.util.HashSet");
            imports.add("java.util.Set");
        }
        if (type.contains(".") && !type.startsWith("java.lang")) {
            String cleanType = type.replaceAll("<.*>", "");
            imports.add(cleanType);
        }
    }

    protected String getDefaultForType(String type, String name, String fullType) {
        if (fullType.contains(".enums.") || fullType.contains(".enum.")) {
            String simpleType = type.contains(".") ? type.substring(type.lastIndexOf(".") + 1) : type;
            return simpleType + ".USER";
        }

        String baseType = type.contains("<") ? type.substring(0, type.indexOf("<")) : type;

        return switch (baseType) {
            case "String" -> {
                if (name.toLowerCase().contains("email")) {
                    yield "\"" + name.toLowerCase() + "\" + number + \"@gmail.com\"";
                } else if (name.toLowerCase().contains("password")) {
                    yield "\"password\" + number";
                } else {
                    yield "\"" + capitalize(name) + " \" + number";
                }
            }
            case "int", "Integer" -> name.toLowerCase().contains("pointer") ? "number * 10" : "number";
            case "long", "Long" -> "(long) number";
            case "double", "Double" -> "(double) number";
            case "boolean", "Boolean" -> "number % 2 == 0";
            case "LocalDate" -> name.toLowerCase().contains("streak")
                    ? "LocalDate.now().minusDays(number)"
                    : "LocalDate.now()";
            case "LocalDateTime" -> "LocalDateTime.now()";
            case "Set", "HashSet" -> "new HashSet<>()";
            case "List", "ArrayList" -> "new ArrayList<>()";
            case "UUID" -> "UUID.nameUUIDFromBytes((\"" + name.toLowerCase() + "-\" + number).getBytes())";
            default -> {
                if (type.contains("List") || type.contains("Set")) {
                    yield "new ArrayList<>()";
                }
                yield "null";
            }
        };
    }
}