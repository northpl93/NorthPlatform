package pl.north93.zgame.api.global.utils.lang;

import java.util.function.Consumer;

public final class CatchException
{
    @FunctionalInterface
    public static interface ExceptionRunnable
    {
        void run() throws Throwable;
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
        catchThrowable(runnable, t -> printStackTrace(t, message));
    }
    
    private static void printStackTrace(Throwable throwable, String message)
    {
        System.err.println(message);
        throwable.printStackTrace();
    }
    
    public static void printStackTrace(final ExceptionRunnable runnable)
    {
        catchThrowable(runnable, t -> t.printStackTrace());
    }
    
    public static void sneaky(final ExceptionRunnable runnable)
    {
        catchThrowable(runnable, t -> SneakyThrow.sneaky(t));
    }
}
