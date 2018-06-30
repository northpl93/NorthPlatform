package pl.north93.zgame.api.global.utils.lang;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
        try ( StringWriter sw = new StringWriter(); PrintWriter out = new PrintWriter(sw) )
        {
            if ( message != null )
            {
                out.println(message);
            }
            
            throwable.printStackTrace(out);
            System.err.println(sw.getBuffer().toString());
        }
        catch ( IOException e )
        {
            SneakyThrow.sneaky(e);
        }
    }
    
    public static void printStackTrace(final ExceptionRunnable runnable)
    {
        catchThrowable(runnable, t -> printStackTrace(t, null));
    }
    
    public static void log(ExceptionRunnable runnable, java.util.logging.Logger logger, String message)
    {
        catchThrowable(runnable, e -> logger.log(java.util.logging.Level.SEVERE, message, e));
    }
    
    public static void log(ExceptionRunnable runnable, java.util.logging.Logger logger)
    {
        catchThrowable(runnable, e -> logger.log(java.util.logging.Level.SEVERE, "", e));
    }
    
    public static void log(ExceptionRunnable runnable, org.apache.logging.log4j.Logger logger, String message)
    {
        catchThrowable(runnable, e -> logger.log(org.apache.logging.log4j.Level.ERROR, message, e));
    }
    
    public static void log(ExceptionRunnable runnable, org.apache.logging.log4j.Logger logger)
    {
        catchThrowable(runnable, e -> logger.log(org.apache.logging.log4j.Level.ERROR, e));
    }
    
    public static void sneaky(final ExceptionRunnable runnable)
    {
        catchThrowable(runnable, t -> SneakyThrow.sneaky(t));
    }
}
