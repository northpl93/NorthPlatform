package pl.north93.zgame.api.global.redis.messaging.impl;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateGeneric;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

// Umożliwia odczytanie konkretnego typu gdy field jest interfejsem
@SuppressWarnings("unchecked")
class DynamicTemplate<T> implements TemplateGeneric<T>
{
    private static final Map<String, Class<?>> classCache = new HashMap<>();
    private Class<?>[] genericTypes;
    private ParameterizedType parameterizedType;

    private static Class<?> findClass(final String className)
    {
        Class<?> clazz = classCache.get(className);
        if (clazz == null)
        {
            try
            {
                clazz = Class.forName(className);
            }
            catch (final ClassNotFoundException e)
            {
                for (final IComponentBundle iComponentBundle : API.getApiCore().getComponentManager().getComponents())
                {
                    try
                    {
                        clazz = Class.forName(className, true, iComponentBundle.getClassLoader());
                    }
                    catch (final ClassNotFoundException ignored)
                    {
                    }
                }

                if (clazz == null)
                {
                    throw new RuntimeException("I can't find class " + className);
                }
            }
            classCache.put(className, clazz);
        }
        return clazz;
    }

    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final T object) throws Exception
    {
        packer.packString(object.getClass().getName());
        final Template<T> template = (Template<T>) templateManager.getTemplate(object.getClass());
        if (template instanceof TemplateGeneric)
        {
            TemplateGeneric templateGeneric = (TemplateGeneric) template;
            if (this.genericTypes != null)
            {
                templateGeneric = (TemplateGeneric) templateGeneric.setGenericType(this.genericTypes);
            }
            else if (this.parameterizedType != null)
            {
                templateGeneric = (TemplateGeneric) templateGeneric.setGenericType(this.parameterizedType);
            }
            templateGeneric.serializeObject(templateManager, packer, object);
            return;
        }
        template.serializeObject(templateManager, packer, object);
    }

    @Override
    public T deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final Template<T> template = (Template<T>) templateManager.getTemplate(findClass(unpacker.unpackString()));
        if (template instanceof TemplateGeneric)
        {
            TemplateGeneric templateGeneric = (TemplateGeneric) template;
            if (this.genericTypes != null)
            {
                templateGeneric = (TemplateGeneric) templateGeneric.setGenericType(this.genericTypes);
            }
            else if (this.parameterizedType != null)
            {
                templateGeneric = (TemplateGeneric) templateGeneric.setGenericType(this.parameterizedType);
            }

            return (T) templateGeneric.deserializeObject(templateManager, unpacker);
        }
        return template.deserializeObject(templateManager, unpacker);
    }

    @Override
    public Template<T> setGenericType(final ParameterizedType type)
    {
        this.parameterizedType = type;
        return this;
    }

    @Override
    public Template<T> setGenericType(final Class<?>... genericTypes)
    {
        this.genericTypes = genericTypes;
        return this;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("genericTypes", this.genericTypes).append("parameterizedType", this.parameterizedType).toString();
    }
}
