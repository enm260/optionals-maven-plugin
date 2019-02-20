package enm.maven.plugin.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.yaml.snakeyaml.Yaml;

import enm.maven.plugin.utils.Utils;

@Mojo(name = "verify", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true)
public class VerifyOptionalsMojo extends ListOptionalsMojo {

    public static final String DESC_KEY = "description";

    @Parameter(required = false, defaultValue = "${project.basedir}/optionals.yml")
    protected File optionalsFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<Dependency> invalidOptionals = getInvalidOptionals();

        if (!invalidOptionals.isEmpty()) {
            getLog().error("The following artifacts have been declared optional but do not have a description in " + optionalsFile.getAbsolutePath());

            for (final Dependency dep : invalidOptionals) {
                getLog().error(Utils.getArtifactInfo(dep));
            }
            throw new MojoFailureException("Artifacts declared optional without an explanation");
        }
    }

    protected List<Dependency> getInvalidOptionals() throws MojoExecutionException {
        final Map<String, Object> groups = parseYaml();
        return Arrays.stream(getOptionals())
                .filter(o -> missingDescription(o, groups))
                .collect(Collectors.toList());
    }

    protected Map<String, Object> parseYaml() throws MojoExecutionException {
        if (optionalsFile == null || !optionalsFile.exists()) {
            return new HashMap<>();
        }

        final Yaml yaml = new Yaml();
        try {
            return yaml.load(new FileInputStream(optionalsFile));
        } catch (final FileNotFoundException e) {
            throw new MojoExecutionException("Error while parsing file " + optionalsFile.getAbsolutePath(), e);
        }
    }

    private boolean missingDescription(final Dependency dep, final Map<String, Object> groups) {
        final String artifactDescription = Utils.getNestedMapValue(groups, dep.getGroupId(), dep.getArtifactId(), DESC_KEY);
        final String groupDescription = Utils.getNestedMapValue(groups, dep.getGroupId(), DESC_KEY);
        return StringUtils.isBlank(artifactDescription) && StringUtils.isBlank(groupDescription);
    }

}
