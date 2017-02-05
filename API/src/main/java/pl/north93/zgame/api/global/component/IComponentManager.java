package pl.north93.zgame.api.global.component;

import java.io.File;
import java.util.Collection;

public interface IComponentManager
{
    void doComponentScan(String componentsYml, ClassLoader classLoader);

    default void doComponentScan(ClassLoader classLoader)
    {
        this.doComponentScan("components.yml", classLoader);
    }

    /**
     * Scans specified directory or file for components.
     *
     * @param file file or directory to be scanned.
     */
    void doComponentScan(File file);

    /**
     * Scans specified classloader for fields which can be injected.
     * This WILL NOT scan for new components.
     *
     * @param classLoader classloader to scan.
     * @param packages packages to scan.
     */
    void performInjectionScan(ClassLoader classLoader, String... packages);

    void setAutoEnable(boolean autoEnable);

    void injectComponent(Object component); // component must be registered in manifest

    default void injectComponents(Object... components)
    {
        for (final Object component : components)
        {
            this.injectComponent(component);
        }
    }

    void enableAllComponents();

    void disableAllComponents();

    <T extends Component> T getComponent(String name);

    Collection<? extends IComponentBundle> getComponents();
}
