package pl.north93.northplatform.api.global.storage;

import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.serializer.platform.reflect.ClassResolver;

public final class NorthPlatformClassResolver implements ClassResolver
{
    @Inject
    private ApiCore apiCore;

    @Override
    public Class<?> findClass(final String name)
    {
        return this.apiCore.getComponentManager().findClass(name);
    }
}
