package enm.maven.plugin.mojo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

import enm.maven.plugin.utils.Utils;

import org.yaml.snakeyaml.Yaml;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true)
public class GenerateOptionalsFileMojo extends VerifyOptionalsMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<Dependency> invalidOptionals = getInvalidOptionals();

        if (!invalidOptionals.isEmpty()) {
            final Map<String, Object> groups = parseYaml();

            for (final Dependency dep : invalidOptionals) {
                Utils.putNestedMapValue(groups, "", dep.getGroupId(), dep.getArtifactId(), DESC_KEY);
            }

            dumpOptionalsYaml(groups);
        }
    }

    private void dumpOptionalsYaml(final Map<String, Object> groups) throws MojoExecutionException {
        final DumperOptions opts = new DumperOptions();
        opts.setIndent(2);
        opts.setPrettyFlow(true);
        opts.setDefaultFlowStyle(FlowStyle.BLOCK);

        final Map<String, Object> sortedGroups = new LinkedHashMap<>();
        groups.entrySet().stream()
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .forEach(e -> sortedGroups.put(e.getKey(), e.getValue()));

        final Yaml yaml = new Yaml(opts);
        try {
            yaml.dump(sortedGroups, new FileWriter(optionalsFile));
        } catch (final IOException e) {
            throw new MojoExecutionException("Error while generating optionals file", e);
        }
    }

}
