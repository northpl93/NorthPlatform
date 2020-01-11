package pl.north93.northplatform.api.global.component.impl.container;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.north93.northplatform.api.global.component.impl.injection.IInjectionContext;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class AbstractBeanContainer
{
    private final Class<?> type;
    private final String   name;

    public final Class<?> getType()
    {
        return this.type;
    }

    public final String getName()
    {
        return this.name;
    }

    public abstract Object getValue(IInjectionContext injectionContext);
}
