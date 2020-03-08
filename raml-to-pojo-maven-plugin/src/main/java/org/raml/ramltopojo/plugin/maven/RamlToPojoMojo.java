/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.ramltopojo.plugin.maven;

import amf.client.model.document.Document;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.raml.ramltopojo.RamlLoader;
import org.raml.ramltopojo.RamlToPojo;
import org.raml.ramltopojo.RamlToPojoBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;
import static org.raml.ramltopojo.TypeFetchers.fromAnywhere;
import static org.raml.ramltopojo.TypeFinders.everyWhere;

@Mojo(name = "generate", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE_PLUS_RUNTIME,
        defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RamlToPojoMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    /**
     * Skip plug-in execution.
     */
    @Parameter(property = "skip", defaultValue = "false")
    private boolean skip;

    /**
     * Target directory for generated Java source files.
     */
    @Parameter(property = "outputDirectory",
            defaultValue = "${project.build.directory}/generated-sources/raml-to-jaxrs-maven-plugin")
    private File outputDirectory;

    /**
     * An array of locations of the RAML file(s).
     */
    @Parameter(property = "ramlFile", required = true)
    private File ramlFile;

    /**
     * The base package for the generated POJOs.
     */
    @Parameter(property = "defaultPackage", required = true)
    private String defaultPackage;

    /**
     * TODO
     */
    @Parameter(property = "basePlugins", required = false)
    private List<String> basePlugins;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (skip) {
            getLog().info("Skipping execution...");
            return;
        }

        if (ramlFile == null) {
            throw new MojoExecutionException("ramlFile is not defined");
        }

        try {
            FileUtils.forceMkdir(outputDirectory);
        } catch (final IOException ioe) {
            throw new MojoExecutionException("Failed to createHandler directory: " + outputDirectory, ioe);
        }

        try {
            project.addCompileSourceRoot(outputDirectory.getPath());

            getLog().info("about to read file " + ramlFile + " in directory " + ramlFile.getParent());

            Document api = RamlLoader.load(ramlFile.toURL().toString());
            RamlToPojo ramlToPojo = RamlToPojoBuilder.builder(api)
                    .inPackage(defaultPackage)
                    .fetchTypes(fromAnywhere())
                    .findTypes(everyWhere()).build(basePlugins);

            ramlToPojo.buildPojos().createAllTypes(outputDirectory.getAbsolutePath());

        } catch (InterruptedException| IOException| ExecutionException e) {

            throw new MojoExecutionException("execution exception", e);
        }
    }
}
