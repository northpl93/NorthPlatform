package pl.north93.zgame.api.standalone;

public final class Launcher
{
    private static StandaloneApp app;
    private static StandaloneApiCore apiCore;

    public static void run(final Class<? extends StandaloneApp> clazz)
    {
        System.out.println("North API standalone app launcher.");

        try
        {
            app = clazz.newInstance();
        }
        catch (final InstantiationException | IllegalAccessException e)
        {
            apiCore.getLogger().severe("Failed to create new app main class!");
            e.printStackTrace();
        }

        apiCore = new StandaloneApiCore(app);
        apiCore.startCore(); // and start application

        Runtime.getRuntime().addShutdownHook(new Thread(() -> apiCore.stopCore())); // TODO is it good?
    }
}
