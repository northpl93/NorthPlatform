package pl.north93.northplatform.api.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import pl.north93.northplatform.api.global.utils.SimpleCallback;

public class SimpleSyncCallback extends SimpleCallback implements ISyncCallback
{
    private static Plugin PLUGIN = Bukkit.getPluginManager().getPlugin("API");
    
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
