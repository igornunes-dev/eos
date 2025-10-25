package org.example.generator;

import java.util.HashMap;
import java.util.Map;

public class GenerationParams {
    private String className;
    private String packageName;
    private boolean overwrite;
    private Map<String, Object> customParams;

    private GenerationParams(Builder builder) {
        this.className = builder.className;
        this.packageName = builder.packageName;
        this.overwrite = builder.overwrite;
        this.customParams = builder.customParams;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public Object getCustomParam(String key) {
        return customParams.get(key);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String className;
        private String packageName;
        private boolean overwrite;
        private Map<String, Object> customParams = new HashMap<>();

        public Builder className(String className) {
            this.className = className;
            return this;
        }

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder overwrite(boolean overwrite) {
            this.overwrite = overwrite;
            return this;
        }

        public Builder customParams(Map<String, Object> customParams) {
            this.customParams = customParams;
            return this;
        }

        public GenerationParams build() {
            return new GenerationParams(this);
        }
    }
}