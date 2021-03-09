package io.protostuff.compiler.maven;

import static java.util.Collections.singletonList;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

import io.protostuff.compiler.model.ImmutableModuleConfiguration;
import io.protostuff.compiler.model.ModuleConfiguration;
import io.protostuff.generator.CompilerModule;
import io.protostuff.generator.ProtostuffCompiler;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * StringTemplate 4 generator MOJO.
 *
 * @author Kostiantyn Shchepanovskyi
 */
@Mojo(name = "st4",
        threadSafe = true,
        configurator = "include-project-dependencies",
        requiresDependencyResolution = COMPILE_PLUS_RUNTIME)
public class St4GeneratorMojo extends AbstractGeneratorMojo {

    @Parameter
    private File target;

    @Parameter
    private String template;

    @Parameter
    private List<String> templates;

    @Parameter
    private String extensions;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        final ProtostuffCompiler compiler = new ProtostuffCompiler();
        final String output = computeSourceOutputDir(target);
        final Set<String> allTemplates = new LinkedHashSet<>();
        if (template != null) {
            allTemplates.add(template);
        }
        if (templates != null) {
            allTemplates.addAll(templates);
        }
        final List<String> protoFiles = new ArrayList<>();
        final List<Path> sourcePathList = getSourcePath();
        for (Path sourcePath : sourcePathList) {
            protoFiles.addAll(findProtoFiles(sourcePath));
        }
        ModuleConfiguration moduleConfiguration = ImmutableModuleConfiguration.builder()
                .name("java")
                .includePaths(sourcePathList)
                .generator(CompilerModule.ST4_COMPILER)
                .putOptions(CompilerModule.TEMPLATES_OPTION, allTemplates)
                .putOptions(CompilerModule.EXTENSIONS_OPTION, extensions)
                .output(output)
                .addAllProtoFiles(protoFiles)
                .build();
        compiler.compile(moduleConfiguration);
        addGeneratedSourcesToProject(output);
    }

}
