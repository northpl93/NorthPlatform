package pl.north93.zgame.controller.launcher;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.plugin.Plugin;
import pl.north93.zgame.controller.NetworkControllerCore;

public class BungeePlugin extends Plugin
{
    private final NetworkControllerCore networkControllerCore = new NetworkControllerCore();

    @Override
    public void onEnable()
    {
        this.getLogger().warning("Network controller is starting as Bungee plugin. This isn't recommended for production environments.");
        this.networkControllerCore.start();
    }

    @Override
    public void onDisable()
    {
        this.networkControllerCore.stop();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("networkControllerCore", this.networkControllerCore).toString();
    }
}
