package enm.maven.plugin.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.maven.model.Dependency;

public class Utils {

    public static String getArtifactInfo(final Dependency dep) {
        final String[] info = new String[] { dep.getGroupId(), dep.getArtifactId(), dep.getVersion() };
        return "    " + String.join(":", info);
    }

    public static String getNestedMapValue(final Map<String, Object> initialMap, final String... keys) {
        final Map<String, Object> map = mostNestedMap(initialMap, false, keys);
        if (map == null)
            return null;

        final Object value = map.get(keys[keys.length - 1]);
        return (value == null ? null : value.toString());
    }

    public static void putNestedMapValue(final Map<String, Object> initialMap, final String value, final String... keys) {
        final Map<String, Object> map = mostNestedMap(initialMap, true, keys);
        map.put(keys[keys.length - 1], value);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> mostNestedMap(final Map<String, Object> initialMap, final boolean insertMaps, final String... keys) {
        Map<String, Object> map = initialMap;
        for (int i = 0; i < keys.length - 1; i++) {
            if (map.get(keys[i]) == null) {
                if (insertMaps)
                    map.put(keys[i], new LinkedHashMap<String, Object>());
                else
                    return null;
            }

            map = (Map<String, Object>) map.get(keys[i]);
        }
        return map;
    }

}
