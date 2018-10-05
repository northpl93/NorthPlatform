package pl.arieals.api.minigame.server.gamehost;

import static pl.north93.zgame.api.global.utils.exceptions.SingletonException.checkSingleton;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.player.PlayerDataManager;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public final class MiniGameApi
{
    private static MiniGameApi INSTANCE;
    @Inject
    private MiniGameServer    server;
    @Inject
    private PlayerDataManager playerDataManager;

    MiniGameApi() // package-local constructor
    {
        checkSingleton(INSTANCE, "INSTANCE");
        INSTANCE = this;
    }

    @Nullable
    public static LocalArena getArena(final Player player)
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        return manager.getArenaManager().getArenaAssociatedWith(player.getUniqueId()).orElse(null);
    }

    @Nullable
    public static LocalArena getArena(final World world)
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        return manager.getArenaManager().getArena(world);
    }

    @Nonnull
    public static List<LocalArena> getArenas()
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        return manager.getArenaManager().getArenas();
    }

    @Nullable
    public static <T> T getPlayerData(final Player player, final Class<T> clazz)
    {
        return INSTANCE.playerDataManager.getPlayerData(player, clazz);
    }

    public static void setPlayerData(final Player player, final Object data)
    {
        INSTANCE.playerDataManager.setPlayerData(player, data);
    }

    public static <T> void setPlayerData(final Player player, final Class<T> clazz, final T data)
    {
        INSTANCE.playerDataManager.setPlayerData(player, clazz, data);
    }

    @Nullable
    public static PlayerStatus getPlayerStatus(final INorthPlayer player)
    {
        return INSTANCE.playerDataManager.getStatus(player);
    }

    public static void setPlayerStatus(final INorthPlayer player, final PlayerStatus newStatus)
    {
        INSTANCE.playerDataManager.updateStatus(player, newStatus);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
