package pl.north93.northplatform.api.global.utils.lang;

import java.util.function.Consumer;

import org.slf4j.Logger;

public final class CatchException
{
    @FunctionalInterface
    public static interface ExceptionRunnable
    {
        void run() throws Throwable;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void sneaky(final Throwable e) throws T
    {
        throw (T) e;
    }

    public static void catchThrowable(final ExceptionRunnable runnable, final Consumer<Throwable> handler)
    {
        try
        {
            runnable.run();
        }
        catch ( Throwable t )
        {
            handler.accept(t);
        }
    }
    
    public static void printStackTrace(final ExceptionRunnable runnable, final String message)
    {
        final Logger logger = ClassUtils.getCallingLogger();
        catchThrowable(runnable, t -> printStackTrace(logger, t, message));
    }
    
    public static void printStackTrace(final ExceptionRunnable runnable)
    {
        final Logger logger = ClassUtils.getCallingLogger();
        catchThrowable(runnable, t -> printStackTrace(logger, t, null));
    }
    
    public static void sneaky(final ExceptionRunnable runnable)
    {
        catchThrowable(runnable, t -> sneaky(t));
    }

    private static void printStackTrace(Logger logger, Throwable throwable, String message)
    {
        logger.error(message, throwable);
    }
}
