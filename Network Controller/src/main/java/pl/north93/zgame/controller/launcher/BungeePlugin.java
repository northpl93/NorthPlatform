package pl.north93.zgame.controller.launcher;

import net.md_5.bungee.api.plugin.Plugin;
import pl.north93.zgame.api.global.API;

public class BungeePlugin extends Plugin
{
    @Override
    public void onEnable()
    {
        this.getLogger().warning("Network controller is starting as Bungee plugin. This isn't recommended for production environments.");
        API.getApiCore().getComponentManager().doComponentScan("components_nc.yml", this.getClass().getClassLoader());
    }
}
