package pl.north93.zgame.api.global.component;

import java.io.File;
import java.util.Collection;

import org.reflections.Reflections;

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

    void setAutoEnable(boolean autoEnable);

    void enableAllComponents();

    void disableAllComponents();

    <T extends Component> T getComponent(String name);

    Collection<? extends IComponentBundle> getComponents();

    /**
     * Wystawia dostep do wewnetrznej instancji Reflections dla danego classloadera.
     * @return instancja Reflections dla danego classloadera.
     */
    Reflections accessReflections(ClassLoader classLoader);
}
