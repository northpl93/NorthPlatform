package pl.north93.northplatform.api.global.serializer.platform.template;

import java.lang.reflect.Type;

import lombok.ToString;

@ToString
public final class AnyInheritedTypeFilter implements TemplateFilter
{
    private final Class<?> type;

    public AnyInheritedTypeFilter(final Class<?> type)
    {
        this.type = type;
    }

    @Override
    public int getPriority()
    {
        return TemplatePriority.HIGH;
    }

    @Override
    public boolean isApplicableTo(final TemplateEngine templateEngine, final Type type)
    {
        final Class<?> other = templateEngine.getRawClassFromType(type);
        return this.type.isAssignableFrom(other);
    }
}
