package pl.north93.zgame.api.global.component;

import java.util.List;

public interface IAnnotatedExtensionPoint extends IExtensionPoint
{
    @Override
    List<IAnnotated> getImplementations();

    default void setAnnotatedHandler(final IExtensionHandler<IAnnotated> handler)
    {
        //noinspection unchecked
        this.setHandler(handler);
    }
}
