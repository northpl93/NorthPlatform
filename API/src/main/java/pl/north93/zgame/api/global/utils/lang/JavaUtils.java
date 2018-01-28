package pl.north93.zgame.api.global.utils.lang;

import java.util.concurrent.Callable;

public final class JavaUtils
{
    private JavaUtils()
    {
    }

    public static <T> T instanceOf(final Object object, final Class<T> clazz)
    {
        if (object == null)
        {
            return null;
        }
        if (clazz.isAssignableFrom(object.getClass()))
        {
            return (T) object;
        }
        return null;
    }

    /**
     * Sluzy do wytlumiania checked exceptionow.<br>
     * {@code final Object sth = hideException(() -> metodaThrowujacaCheckedException());}
     *
     * @param code kod, najczesciej metoda w ktorej wygluszamy checked exception.
     * @param <T> zwracany typ przez metode.
     * @return wynik metody.
     */
    public static <T> T hideException(final Callable<T> code)
    {
        try
        {
            return code.call();
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface ExceptionalRunnable
    {
        void run() throws Exception;
    }

    public static void hideException(final ExceptionalRunnable code)
    {
        try
        {
            code.run();
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
