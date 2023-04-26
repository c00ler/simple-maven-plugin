package com.github.avenderov;

import com.gradle.maven.extension.api.cache.BuildCacheApi;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.List;
import java.util.Optional;

@Mojo(name = "dependency-counter", defaultPhase = LifecyclePhase.INITIALIZE)
public class DependencyCounterMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession session;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<Dependency> dependencies = project.getDependencies();
        getLog().info("Number of dependencies: " + dependencies.size());

        getLog().info("--- Gradle Enterprise ---");
        testGradleEnterprise();
    }

    private void testGradleEnterprise() {
        lookupBuildCacheApi().ifPresent(o -> {
            BuildCacheApi buildCacheApi = (BuildCacheApi) o;
            getLog().info("BuildCacheApi found");
        });
    }

    private Optional<Object> lookupBuildCacheApi() {
        try {
            return Optional.of(session.lookup("com.gradle.maven.extension.api.cache.BuildCacheApi"));
        } catch (ComponentLookupException e) {
            getLog().info("BuildCacheApi not found");
            return Optional.empty();
        }
    }
}
