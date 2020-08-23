package pl.north93.northplatform.api.minigame.server.gamehost;

import static pl.north93.northplatform.api.global.utils.exceptions.SingletonException.checkSingleton;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.player.PlayerDataManager;
import pl.north93.northplatform.api.minigame.shared.api.PlayerStatus;

public final class MiniGameApi
{
    private static MiniGameApi INSTANCE;
    @Inject
    private LocalArenaManager localArenaManager;
    @Inject
    private PlayerDataManager playerDataManager;

    @Bean
    MiniGameApi() // package-local constructor
    {
        checkSingleton(INSTANCE, "INSTANCE");
        INSTANCE = this;
    }

    @Nullable
    public static LocalArena getArena(final Player player)
    {
        final LocalArenaManager arenaManager = INSTANCE.localArenaManager;
        return arenaManager.getArenaAssociatedWith(player.getUniqueId()).orElse(null);
    }

    @Nullable
    public static LocalArena getArena(final World world)
    {
        final LocalArenaManager arenaManager = INSTANCE.localArenaManager;
        return arenaManager.getArena(world);
    }

    @Nonnull
    public static List<LocalArena> getArenas()
    {
        final LocalArenaManager arenaManager = INSTANCE.localArenaManager;
        return arenaManager.getArenas();
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
