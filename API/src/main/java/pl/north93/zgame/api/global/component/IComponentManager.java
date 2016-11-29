package pl.north93.zgame.api.global.component;

public interface IComponentManager
{
    void doComponentScan(ClassLoader classLoader);

    void setAutoEnable(boolean autoEnable);

    void enableAllComponents();

    void disableAllComponents();
}
