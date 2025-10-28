package org.example.generator;

import org.example.core.ProjectContext;

import java.io.IOException;
import java.nio.file.Path;

public interface CodeGenerate {
    GenerationResult generate(ProjectContext context, GenerationParams params) throws IOException;
    default boolean fileExists(Path filePath) {
        return filePath.toFile().exists();
    }
}
