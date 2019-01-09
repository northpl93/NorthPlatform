package pl.north93.northplatform.api.global.serializer.platform.template;

import java.lang.reflect.Type;

import lombok.ToString;

@ToString
public final class ExactTypeIgnoreGenericFilter implements TemplateFilter
{
    private final Class<?> clazz;
    private final int      priority;

    public ExactTypeIgnoreGenericFilter(final Class<?> clazz, final int priority)
    {
        this.priority = priority;
        this.clazz = clazz;
    }

    public ExactTypeIgnoreGenericFilter(final Class<?> clazz)
    {
        this(clazz, TemplatePriority.NORMAL);
    }

    @Override
    public int getPriority()
    {
        return this.priority;
    }

    @Override
    public boolean isApplicableTo(final TemplateEngine templateEngine, final Type type)
    {
        return templateEngine.getRawClassFromType(type) == this.clazz;
    }
}
