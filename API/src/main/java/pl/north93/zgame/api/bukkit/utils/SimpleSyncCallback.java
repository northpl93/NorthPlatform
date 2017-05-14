package pl.north93.zgame.api.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.utils.SimpleCallback;

public class SimpleSyncCallback extends SimpleCallback implements ISyncCallback
{
    private static final Plugin PLUGIN = ((BukkitApiCore) API.getApiCore()).getPluginMain();
    
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
