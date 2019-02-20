package enm.maven.plugin.mojo;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import enm.maven.plugin.utils.Utils;

@Mojo(name = "list", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true)
public class ListOptionalsMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Dependency[] optionals = getOptionals();
        for (final Dependency dep : optionals) {
            final String info = Utils.getArtifactInfo(dep);
            getLog().info(info);
        }
    }

    @SuppressWarnings("unchecked")
    protected Dependency[] getOptionals() {
        return ((List<Dependency>) project.getDependencies()).stream()
                .filter(d -> d.isOptional())
                .sorted((d1, d2) -> {
                    if (!StringUtils.equals(d1.getGroupId(), d2.getGroupId()))
                        return d1.getGroupId().compareTo(d2.getGroupId());
                    return d1.getArtifactId().compareTo(d2.getArtifactId());
                })
                .toArray(size -> new Dependency[size]);
    }

}
