package pl.arieals.minigame.goldhunter.utils;

import java.util.function.Consumer;

public class CatchException
{
    private CatchException()
    {
    }
    
    public static void printStackTrace(Runnable runnable)
    {
        catchException(runnable, t -> t.printStackTrace());
    }
    
    public static void catchException(Runnable runnable, Consumer<Throwable> exceptionHandler)
    {
        try
        {
            runnable.run();
        }
        catch ( Throwable e )
        {
            exceptionHandler.accept(e);
        }
    }
}
