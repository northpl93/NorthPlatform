package pl.north93.northplatform.api.minigame.server.gamehost.arena.player;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.SpectatorModeChangeEvent;
import pl.north93.northplatform.api.minigame.shared.api.PlayerStatus;

public class PlayerDataManager
{
    @Inject
    private LocalArenaManager localArenaManager;
    @Inject
    private IBukkitServerManager serverManager;

    @Bean
    private PlayerDataManager()
    {
    }

    public PlayerStatus getStatus(final INorthPlayer player)
    {
        return player.getPlayerData(PlayerStatus.class);
    }

    public void updateStatus(final INorthPlayer player, final PlayerStatus newStatus)
    {
        final LocalArena arena = this.localArenaManager.getArenaAssociatedWith(player.getUniqueId())
                                                       .orElseThrow(IllegalStateException::new);

        final PlayerStatus oldStatus = this.getStatus(player);
        final Set<INorthPlayer> spectators = arena.getPlayersManager().getSpectators();

        if (newStatus == PlayerStatus.SPECTATOR && ! spectators.contains(player))
        {
            throw new IllegalArgumentException("You can't set SPECTATOR status for player that isn't spectating.");
        }
        else if (newStatus != PlayerStatus.SPECTATOR && spectators.contains(player))
        {
            throw new IllegalArgumentException("You can't set any other status than SPECTATING for spectating player.");
        }

        player.setPlayerData(PlayerStatus.class, newStatus);
        this.serverManager.callEvent(new SpectatorModeChangeEvent(arena, player, oldStatus, newStatus));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
