package pl.north93.zgame.api.global.component;

import java.util.Set;

public interface IComponentBundle
{
    Set<String> getBasePackages();

    String getName();

    ComponentStatus getStatus();

    /**
     * Builtin components are integrated in main API jar.
     *
     * @return true if component is builtin.
     *         false otherwise.
     */
    boolean isBuiltinComponent();

    ComponentDescription getDescription();

    ClassLoader getClassLoader();

    IBeanContext getBeanContext();
}
