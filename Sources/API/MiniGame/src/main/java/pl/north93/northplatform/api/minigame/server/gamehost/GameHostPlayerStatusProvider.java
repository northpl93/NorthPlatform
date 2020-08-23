package pl.north93.northplatform.api.minigame.server.gamehost;

import java.util.UUID;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.north93.northplatform.api.minigame.server.shared.status.IPlayerStatusProvider;
import pl.north93.northplatform.api.minigame.shared.api.arena.IArena;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.northplatform.api.minigame.shared.api.status.InGameStatus;

public class GameHostPlayerStatusProvider implements IPlayerStatusProvider
{
    private final GameHostManager gameHostManager;
    private final LocalArenaManager localArenaManager;

    @Bean
    public GameHostPlayerStatusProvider(final GameHostManager gameHostManager, final LocalArenaManager localArenaManager)
    {
        this.gameHostManager = gameHostManager;
        this.localArenaManager = localArenaManager;
    }

    @Override
    public IPlayerStatus getLocation(final Player player)
    {
        final UUID arenaId = this.localArenaManager.getArenaAssociatedWith(player.getUniqueId()).map(IArena::getId).orElse(null);
        return new InGameStatus(this.gameHostManager.getServerId(), arenaId, this.gameHostManager.getMiniGameConfig().getGameIdentity());
    }
}
