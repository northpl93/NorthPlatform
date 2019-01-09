package pl.north93.northplatform.antycheat.timeline.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.Main;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

// publiczna zeby bukkit mogl uzywac MethodHandles w evencie
public class TimelineRemover implements AutoListener
{
    @Inject
    private BukkitApiCore bukkitApiCore;

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        final Main pluginMain = this.bukkitApiCore.getPluginMain();
        event.getPlayer().removeMetadata("AntyCheat.Timeline", pluginMain);
    }
}
