package pl.arieals.api.minigame.server.gamehost.listener;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectMessages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class PlayerListener implements Listener
{
    @InjectMessages("MiniGameApi")
    private MessagesBox    messages;
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        final GameHostManager gameHostManager = this.server.getServerManager();
        final Optional<LocalArena> arena = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());

        if (arena.isPresent())
        {
            final PlayersManager playersManager = arena.get().getPlayersManager();
            playersManager.playerConnected(player);
            this.announceJoinLeft(playersManager, player, true);
        }
        else
        {
            // TODO: Jeżeli gracz dołączy do gamehosta, ale nie będzie miał powiązanej areny
            //       to należy go ukryć przed wszystkimi graczami którzy są na arenach.
            //       Potrzebne jest to aby moderatorzy mogli nadzorować graczy bez wpływu na rozgrywkę
            player.sendMessage("Dolaczyles do GameHosta, ale nie znaleziono powiazanej areny"); // debug msg
            return;
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();

        final GameHostManager gameHostManager = this.server.getServerManager();
        final Optional<LocalArena> arena = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());

        if (! arena.isPresent())
        {
            return;
        }

        final PlayersManager playersManager = arena.get().getPlayersManager();
        playersManager.playerDisconnected(player);
        this.announceJoinLeft(playersManager, player, false);
    }

    private void announceJoinLeft(final PlayersManager manager, final Player player, final boolean join)
    {
        final GameHostManager gameHostManager = this.server.getServerManager();

        final String msgKey = join ? "player.joined_arena" : "player.quit_arena";
        final String name = player.getName();
        final int playersCount = manager.getPlayers().size();
        final int maxPlayers = gameHostManager.getMiniGameConfig().getSlots();

        manager.broadcast(this.messages, msgKey, name, playersCount, maxPlayers);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
