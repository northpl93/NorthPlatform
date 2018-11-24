package pl.north93.northplatform.api.minigame.server.lobby.hub.visibility;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

public interface IHubVisibilityPolicy
{
    boolean visible(INorthPlayer observer, INorthPlayer target);
}
