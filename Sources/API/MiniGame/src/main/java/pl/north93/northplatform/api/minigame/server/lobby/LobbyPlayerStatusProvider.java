package pl.north93.northplatform.api.minigame.server.lobby;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.minigame.server.shared.status.IPlayerStatusProvider;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.northplatform.api.minigame.shared.api.status.InHubStatus;

public class LobbyPlayerStatusProvider implements IPlayerStatusProvider
{
    private final LobbyManager lobbyManager;

    @Bean
    public LobbyPlayerStatusProvider(final LobbyManager lobbyManager)
    {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public IPlayerStatus getLocation(final Player player)
    {
        final String hubId = this.lobbyManager.getLocalHub().getHubWorld(player).getHubId();
        return new InHubStatus(this.lobbyManager.getServerId(), hubId);
    }
}
