package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.List;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.player.SpectatorModeChangeEvent;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PlayerDataManager
{
    @Inject
    private BukkitApiCore  apiCore;
    @Inject
    private MiniGameServer miniGameServer;

    @Bean
    private PlayerDataManager()
    {
    }

    public <T> T getPlayerData(final Player player, final Class<T> clazz)
    {
        Preconditions.checkNotNull(player, "Player can't be null in getPlayerData");
        final List<MetadataValue> metadata = player.getMetadata(clazz.getName());
        if (metadata.isEmpty())
        {
            return null;
        }
        //noinspection unchecked
        return (T) metadata.get(0).value();
    }

    public void setPlayerData(final Player player, final Object data)
    {
        Preconditions.checkNotNull(player, "Player can't be null in setPlayerData");
        player.setMetadata(data.getClass().getName(), new FixedMetadataValue(this.apiCore.getPluginMain(), data));
    }

    public PlayerStatus getStatus(final Player player)
    {
        final List<MetadataValue> minigameApiStatus = player.getMetadata("minigameApiStatus");
        if (minigameApiStatus.isEmpty())
        {
            return null;
        }
        return (PlayerStatus) minigameApiStatus.get(0).value();
    }

    public void updateStatus(final Player player, final PlayerStatus newStatus)
    {
        final LocalArenaManager arenaManager = this.getGameHost().getArenaManager();
        final LocalArena arena = arenaManager.getArenaAssociatedWith(player.getUniqueId())
                                             .orElseThrow(IllegalStateException::new);

        final PlayerStatus oldStatus = this.getStatus(player);
        final List<Player> spectators = arena.getPlayersManager().getSpectators();

        if (newStatus == PlayerStatus.SPECTATOR && ! spectators.contains(player))
        {
            throw new IllegalArgumentException("You can't set SPECTATOR status for player that isn't spectating.");
        }
        else if (newStatus != PlayerStatus.SPECTATOR && spectators.contains(player))
        {
            throw new IllegalArgumentException("You can't set any other status than SPECTATING for spectating player.");
        }

        player.setMetadata("minigameApiStatus", new FixedMetadataValue(this.apiCore.getPluginMain(), newStatus));
        this.apiCore.callEvent(new SpectatorModeChangeEvent(arena, player, oldStatus, newStatus));
    }

    private GameHostManager getGameHost()
    {
        return this.miniGameServer.getServerManager();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
