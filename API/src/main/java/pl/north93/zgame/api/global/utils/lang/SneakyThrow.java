package pl.north93.zgame.api.global.utils.lang;

public class SneakyThrow
{
    private SneakyThrow()
    {
    }
    
    public static <R> R sneaky(ExceptionSupplier<R> supplier) 
    {
        try
        {
            return supplier.tryRun();
        }
        catch ( Throwable e )
        {
            sneaky(e);
            return null;
        }
    }
    
    public static void sneaky(Throwable e)
    {
        SneakyThrow.<RuntimeException>sneaky0(e);
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void sneaky0(Throwable e) throws T
    {
        throw (T) e;
    }
    
    @FunctionalInterface
    public static interface ExceptionSupplier<R>
    {
        R tryRun() throws Throwable;
    }
}
