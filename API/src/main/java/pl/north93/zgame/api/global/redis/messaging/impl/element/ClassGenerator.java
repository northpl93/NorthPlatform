package pl.north93.zgame.api.global.redis.messaging.impl.element;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import pl.north93.zgame.api.global.redis.messaging.Template;

class ClassGenerator // TODO
{
    public static final ClassGenerator INSTANCE = new ClassGenerator();
    private final Map<Field, ITemplateElement> cache = new HashMap<>(32);

    public ITemplateElement getTemplateElement(final Class<?> clazz, final Field field, final Template template)
    {
        final ITemplateElement fromCache = this.cache.get(field);
        if (fromCache != null)
        {
            return fromCache;
        }
        return null;
    }

    /*private Class<?> generateClass()
    {
        final ClassPool pool = ClassPool.getDefault();
        pool.makeClass("");
        CtClass ctClass =
    }*/
}
