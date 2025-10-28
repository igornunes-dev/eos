package org.example.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class TemplateEngine {
    private final Configuration config;

    public TemplateEngine() {
        this.config = new Configuration(Configuration.VERSION_2_3_32);
        this.config.setClassForTemplateLoading(this.getClass(), "/templates");
        this.config.setDefaultEncoding("UTF-8");
    }

    public String process(String templateName, Map<String, Object> data) throws IOException {
        try {
            Template template = config.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            return writer.toString();
        } catch (TemplateException e) {
            throw new IOException("Error processing template: " + templateName, e);
        }
    }
}
