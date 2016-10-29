package pl.north93.zgame.controller.launcher;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.standalone.Launcher;
import pl.north93.zgame.api.standalone.StandaloneApiCore;
import pl.north93.zgame.api.standalone.StandaloneApp;
import pl.north93.zgame.controller.NetworkControllerCore;

public class StandaloneController extends StandaloneApp
{
    private final NetworkControllerCore networkControllerCore = new NetworkControllerCore();

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
    public void start(final StandaloneApiCore apiCore)
    {
        this.networkControllerCore.start();
    }

    @Override
    public void stop()
    {
        this.networkControllerCore.stop();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("networkControllerCore", this.networkControllerCore).toString();
    }
}
