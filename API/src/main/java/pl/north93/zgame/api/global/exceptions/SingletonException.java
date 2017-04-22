package pl.north93.zgame.api.global.exceptions;

public class SingletonException extends RuntimeException
{
    public SingletonException(final String fieldName)
    {
        super("You can't set field " + fieldName + " because it's singleton.");
    }

    public static void checkSingleton(final Object field, final String fieldName) throws SingletonException
    {
        if (field != null)
        {
            throw new SingletonException(fieldName);
        }
    }
}
