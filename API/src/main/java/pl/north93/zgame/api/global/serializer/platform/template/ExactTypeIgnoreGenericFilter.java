package pl.north93.zgame.api.global.serializer.platform.template;

import java.lang.reflect.Type;

import lombok.ToString;

@ToString
public class ExactTypeIgnoreGenericFilter implements TemplateFilter
{
    private final Class<?> clazz;

    public ExactTypeIgnoreGenericFilter(final Class<?> clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public int getPriority()
    {
        return 0;
    }

    @Override
    public boolean isApplicableTo(final TemplateEngine templateEngine, final Type type)
    {
        if (type instanceof Class)
        {
            return type == this.clazz;
        }

        return false;
    }
}
