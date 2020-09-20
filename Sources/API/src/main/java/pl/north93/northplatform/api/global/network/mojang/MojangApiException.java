package pl.north93.northplatform.api.global.network.mojang;

/**
 * Exception thrown when there are issues with connection to api.mojang.com.
 */
public class MojangApiException extends Exception
{
    public MojangApiException(final String message)
    {
        super(message);
    }

    public MojangApiException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
