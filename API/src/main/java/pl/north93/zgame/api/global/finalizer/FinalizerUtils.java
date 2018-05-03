package pl.north93.zgame.api.global.finalizer;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public final class FinalizerUtils
{
    @Inject
    private static IFinalizer instance;

    public static void register(final Object object, final Runnable runnable)
    {
        instance.register(object, runnable);
    }
}
