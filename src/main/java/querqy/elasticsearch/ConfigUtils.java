package querqy.elasticsearch;

import org.elasticsearch.SpecialPermission;
import org.elasticsearch.index.query.QueryShardContext;
import querqy.rewrite.RewriterFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConfigUtils {


    static String getStringArg(final Map<String, Object> config, final String name, final String defaultValue) {
        final String value = (String) config.get(name);
        return value == null ? defaultValue : value;
    }

    static Optional<String> getStringArg(final Map<String, Object> config, final String name) {
        return Optional.ofNullable((String) config.get(name));
    }


    static <T> T getArg(final Map<String, Object> config, final String name, final T defaultValue) {
        return (T) config.getOrDefault(name, defaultValue);
    }

    static <V> V getInstanceFromArg(final Map<String, Object> config, final String name, final V defaultValue) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new SpecialPermission());
        }


        return AccessController.doPrivileged(
            (PrivilegedAction<V>) () -> {
                final String classField = (String) config.get(name);
                if (classField == null) {
                    return defaultValue;
                }

                final String className = classField.trim();
                if (className.isEmpty()) {
                    return defaultValue;
                }

                try {
                    return (V) Class.forName(className).getDeclaredConstructor().newInstance();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            });




    }

}