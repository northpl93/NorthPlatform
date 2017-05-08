package pl.north93.zgame.api.global.utils;

/**
 * Klasa pomocnicza używana do odczytywania aktualnego stacktrace
 */
public class NorthSecurityManager extends SecurityManager
{
    private static final NorthSecurityManager INSTANCE = new NorthSecurityManager();

    public static NorthSecurityManager getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Class[] getClassContext() // protected -> public
    {
        return super.getClassContext();
    }

    public Class<?> getCallerClass(final int i)
    {
        return this.getClassContext()[i + 3];
    }
}
