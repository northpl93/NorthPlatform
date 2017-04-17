package pl.north93.zgame.api.global.component.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import pl.north93.zgame.api.global.component.IExtensionPoint;

class ExtensionPointFactory
{
    static final ExtensionPointFactory INSTANCE = new ExtensionPointFactory();

    @SuppressWarnings("unchecked")
    <T> IExtensionPoint<T> createExtensionPoint(final ClassLoader classLoader, final String extensionPointClass)
    {
        final Class<?> extensionClass;
        try
        {
            extensionClass = Class.forName(extensionPointClass, true, classLoader);
        }
        catch (final ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }

        if (extensionClass.isAnnotation())
        {
            return new AnnotatedExtensionPointImpl((Class<? extends Annotation>) extensionClass);
        }
        else if (extensionClass.isInterface() || Modifier.isAbstract(extensionClass.getModifiers()))
        {
            return new ExtensionPointImpl<T>((Class<T>) extensionClass);
        }

        return null;
    }
}
