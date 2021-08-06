package pl.north93.northplatform.api.global.storage;

import pl.north93.northplatform.api.global.component.IComponentManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.serializer.platform.reflect.ClassResolver;

public final class NorthPlatformClassResolver implements ClassResolver
{
    @Inject
    private IComponentManager componentManager;

    @Override
    public Class<?> findClass(final String name)
    {
        return this.componentManager.findClass(name);
    }
}
