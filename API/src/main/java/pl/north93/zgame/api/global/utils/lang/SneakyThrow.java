package pl.north93.zgame.api.global.utils.lang;

public class SneakyThrow
{
    private SneakyThrow()
    {
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
}
