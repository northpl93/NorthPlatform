package pl.north93.northplatform.api.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.Platform;

public class Main extends JavaPlugin
{
    private final ApiCore apiCore = new ApiCore(Platform.BUKKIT, new BukkitHostConnector(this));

    @Override
    public void onEnable()
    {
        this.apiCore.startPlatform();
    }

    @Override
    public void onDisable()
    {
        this.apiCore.stopPlatform();
    }
}
