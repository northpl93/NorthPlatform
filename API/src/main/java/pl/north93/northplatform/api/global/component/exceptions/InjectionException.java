package pl.north93.northplatform.api.global.component.exceptions;

import static java.text.MessageFormat.format;

public class InjectionException extends RuntimeException
{
    public InjectionException(final String message)
    {
        super(message);
    }

    public InjectionException(final Class<?> injectedType, final Throwable cause)
    {
        super(format("Failed to resolve bean when processing injection: {0}", injectedType.getName()), cause);
    }
}
