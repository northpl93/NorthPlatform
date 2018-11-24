package pl.north93.northplatform.api.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import pl.north93.northplatform.api.global.utils.SimpleCallback;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.global.API;

public class SimpleSyncCallback extends SimpleCallback implements ISyncCallback
{
    private static Plugin PLUGIN = ((BukkitApiCore) API.getApiCore()).getPluginMain();
    
    @Override
    protected void runTask()
    {
        if ( Bukkit.isPrimaryThread() )
        {
            super.runTask();
        }
        else
        {
            Bukkit.getScheduler().runTask(PLUGIN, () -> super.runTask());
        }
    }
}
