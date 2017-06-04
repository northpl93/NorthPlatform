package pl.north93.zgame.api.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.utils.SimpleCallback;

public class SimpleSyncCallback extends SimpleCallback implements ISyncCallback
{
    @Inject
    private static Plugin PLUGIN;
    
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
