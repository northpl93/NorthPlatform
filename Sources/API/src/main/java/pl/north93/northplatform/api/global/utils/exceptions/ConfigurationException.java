package pl.north93.northplatform.api.global.utils.exceptions;

/**
 * Wyjątek rzucany gdy występuje jakiś problem w konfiguracji sieci
 * który uniemożliwia pracę.
 */
public class ConfigurationException extends RuntimeException
{
    public ConfigurationException()
    {
    }

    public ConfigurationException(final String message)
    {
        super(message);
    }

    public ConfigurationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public ConfigurationException(final Throwable cause)
    {
        super(cause);
    }
}
