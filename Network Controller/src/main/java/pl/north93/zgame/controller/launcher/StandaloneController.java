package pl.north93.zgame.controller.launcher;

import pl.north93.zgame.api.standalone.Launcher;
import pl.north93.zgame.api.standalone.StandaloneApiCore;
import pl.north93.zgame.api.standalone.StandaloneApp;

public class StandaloneController extends StandaloneApp
{
    public static void main(final String... args)
    {
        Launcher.run(StandaloneController.class);
    }

    @Override
    public String getId()
    {
        return "controller";
    }

    @Override
    public void init(final StandaloneApiCore apiCore)
    {
    }

    @Override
    public void start(final StandaloneApiCore apiCore)
    {
        apiCore.getComponentManager().doComponentScan("components_nc.yml", this.getClass().getClassLoader());
    }

    @Override
    public void stop()
    {
    }
}
