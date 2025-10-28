package org.example.generator;

import java.nio.file.Path;

public class GenerationResult {
    private boolean success;
    private Path generatedFile;
    private String message;

    private GenerationResult(Builder builder) {
        this.success = builder.success;
        this.generatedFile = builder.generatedFile;
        this.message = builder.message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Path getGeneratedFile() {
        return generatedFile;
    }

    public String getMessage() {
        return message;
    }

    public static GenerationResult success(Path file) {
        return new Builder()
                .success(true)
                .generatedFile(file)
                .message("Generated successfully: " + file.getFileName())
                .build();
    }

    public static GenerationResult error(String message) {
        return new Builder()
                .success(false)
                .message(message)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private Path generatedFile;
        private String message;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder generatedFile(Path generatedFile) {
            this.generatedFile = generatedFile;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public GenerationResult build() {
            return new GenerationResult(this);
        }
    }
}