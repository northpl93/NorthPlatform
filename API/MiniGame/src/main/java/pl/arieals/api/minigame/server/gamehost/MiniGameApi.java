package pl.arieals.api.minigame.server.gamehost;

import static pl.north93.zgame.api.global.exceptions.SingletonException.checkSingleton;


import java.util.List;
import java.util.Optional;

import org.bukkit.World;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public final class MiniGameApi
{
    private static MiniGameApi INSTANCE;
    @Inject
    private MiniGameServer server;

    MiniGameApi() // package-local constructor
    {
        checkSingleton(INSTANCE, "INSTANCE");
        INSTANCE = this;
    }

    public static LocalArena getArena(final Player player)
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        return manager.getArenaManager().getArenaAssociatedWith(player.getUniqueId()).orElse(null);
    }

    public static LocalArena getArena(final World world)
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        return manager.getArenaManager().getArena(world);
    }

    public static List<LocalArena> getArenas()
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        return manager.getArenaManager().getArenas();
    }

    public static <T> T getPlayerData(final Player player, final Class<T> clazz)
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        return manager.getPlayerData(player, clazz);
    }

    public static void setPlayerData(final Player player, final Object data)
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        manager.setPlayerData(player, data);
    }

    public static PlayerStatus getPlayerStatus(final Player player)
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        return manager.getArenaManager().getArenaAssociatedWith(player.getUniqueId())
                      .map(arena -> arena.getPlayersManager().getStatus(player)).orElse(null);
    }

    public static void setPlayerStatus(final Player player, final PlayerStatus newStatus)
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        final Optional<LocalArena> arena = manager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());
        arena.ifPresent(localArena -> localArena.getPlayersManager().updateStatus(player, newStatus));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
