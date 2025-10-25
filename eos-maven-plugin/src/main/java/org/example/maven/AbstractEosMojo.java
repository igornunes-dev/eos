package org.example.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.example.core.ProjectContext;

public abstract class AbstractEosMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(property = "name", required = true)
    protected String name;

    @Parameter(property = "package")
    protected String packageName;

    @Parameter(property = "force", defaultValue = "false")
    protected boolean force;

    protected ProjectContext getProjectContext() throws MojoExecutionException {
        try {
            // Define o diretório do projeto para o ProjectContext
            System.setProperty("user.dir", project.getBasedir().getAbsolutePath());
            return ProjectContext.analyze();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to analyze project: " + e.getMessage(), e);
        }
    }
}