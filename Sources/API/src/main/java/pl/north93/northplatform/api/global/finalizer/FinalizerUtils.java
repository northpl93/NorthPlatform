package pl.north93.northplatform.api.global.finalizer;

import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public final class FinalizerUtils
{
    @Inject
    private static IFinalizer instance;

    public static void register(final Object object, final Runnable runnable)
    {
        instance.register(object, runnable);
    }
}
