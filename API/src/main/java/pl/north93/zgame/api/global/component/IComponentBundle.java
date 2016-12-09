package pl.north93.zgame.api.global.component;

import java.util.Collection;

public interface IComponentBundle
{
    String getName();

    /**
     * Builtin components are integrated in main API jar.
     *
     * @return true if component is builtin.
     *         false otherwise.
     */
    boolean isBuiltinComponent();

    ComponentDescription getDescription();

    ClassLoader getClassLoader();

    Collection<? extends IExtensionPoint<?>> getExtensionPoints();

    <T> IExtensionPoint<T> getExtensionPoint(Class<T> clazz);

    /**
     * Scans this component for implementations of extension points.
     */
    void doExtensionsScan();
}
