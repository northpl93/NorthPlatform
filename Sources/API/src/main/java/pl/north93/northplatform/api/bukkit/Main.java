package pl.north93.northplatform.api.bukkit;

import static pl.north93.northplatform.api.bukkit.BukkitHostConnector.BUKKIT_HOST;


import org.bukkit.plugin.java.JavaPlugin;

import pl.north93.northplatform.api.global.ApiCore;

public class Main extends JavaPlugin
{
    private final ApiCore apiCore = new ApiCore(BUKKIT_HOST, new BukkitHostConnector(this));

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
