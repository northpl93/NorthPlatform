package pl.north93.northplatform.api.global.serializer.platform.template;

import javax.annotation.Nonnull;

import java.lang.reflect.Type;

public interface TemplateFilter extends Comparable<TemplateFilter>
{
    int getPriority();

    boolean isApplicableTo(TemplateEngine templateEngine, Type type);

    @Override
    default int compareTo(final @Nonnull TemplateFilter other)
    {
        final int result = other.getPriority() - this.getPriority();
        if (result == 0)
        {
            return -1;
        }

        return result;
    }
}
