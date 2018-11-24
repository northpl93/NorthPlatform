package pl.north93.groovyscript.platform;

import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.IComponentBundle;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.exceptions.BeanNotFoundException;

public final class BaseProvider
{
    @Inject
    private static ApiCore apiCore;

    public static <T> T inject(final Class<T> clazz)
    {
        for (final IComponentBundle bundle : apiCore.getComponentManager().getComponents())
        {
            try
            {
                return bundle.getBeanContext().getBean(clazz);
            }
            catch (final BeanNotFoundException ignored)
            {
            }
        }

        throw new NullPointerException("Not found bean " + clazz);
    }
}
