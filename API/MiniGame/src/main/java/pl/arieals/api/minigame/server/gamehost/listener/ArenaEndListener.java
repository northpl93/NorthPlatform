package pl.arieals.api.minigame.server.gamehost.listener;

import static java.text.MessageFormat.format;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ArenaEndListener implements Listener
{
    @Inject
    private BukkitApiCore apiCore;

    @EventHandler
    public void stopEmptyArena(final PlayerQuitArenaEvent event)
    {
        if ( event.getArena().getGamePhase() != GamePhase.STARTED )
        {
            // 1. jesli arena jest w lobby to nie ma sensu nic robic
            //    gra po prostu nie wystartuje (zbyt mala ilosc graczy do startu)
            // 2. jesli arena jest w post_game to tez nie trzeba nic robic
            //    bo minigra powinna obsluzyc powrot areny do LOBBY po
            //    jakims czasie
            return;
        }
        
        if (event.getArena().getPlayersManager().getPlayers().size() > 0)
        {
            return;
        }

        this.apiCore.getLogger().info(format("Arena {0} jest pusta, przelaczanie do INITIALISING...", event.getArena().getId()));
        Bukkit.getScheduler().runTaskLater(this.apiCore.getPluginMain(), () ->
        {
            event.getArena().setGamePhase(GamePhase.INITIALISING);
        }, 1);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
