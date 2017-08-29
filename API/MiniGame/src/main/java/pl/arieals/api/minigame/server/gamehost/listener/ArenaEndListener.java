package pl.arieals.api.minigame.server.gamehost.listener;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
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
        final LocalArena arena = event.getArena();
        if (! arena.getPlayersManager().getPlayers().isEmpty())
        {
            return;
        }

        if (arena.getGamePhase() == GamePhase.LOBBY || arena.getGamePhase() == GamePhase.INITIALISING)
        {
            // W lobby nie trzeba nic robic, a podczas initialising wychodzacy
            // gracze nas nie interesuja
            return;
        }

        this.apiCore.getLogger().log(Level.INFO, "Arena {0} is empty, switching to INITIALISING...", arena.getId());
        this.apiCore.run(() ->
        {
            arena.setGamePhase(GamePhase.INITIALISING);
        });
    }

    @EventHandler
    public void switchToInitialisingWhenEndEmpty(final GameEndEvent event)
    {
        final LocalArena arena = event.getArena();
        if (! arena.getPlayersManager().getPlayers().isEmpty())
        {
            return;
        }

        this.apiCore.getLogger().log(Level.INFO, "Arena {0} has been switched to POST_GAME without players, switching to INITIALISING", arena.getId());
        this.apiCore.run(() ->
        {
            // kiedy cos przelaczylo pusta arene do POST_GAME to natychmiast przerzucamy do INITIALISING
            // dopiero przy nastepnym ticku zeby nie zepsuc innej logiki w minigrach
            arena.setGamePhase(GamePhase.INITIALISING);
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
