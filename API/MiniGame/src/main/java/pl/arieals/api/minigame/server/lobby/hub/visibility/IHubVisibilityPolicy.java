package pl.arieals.api.minigame.server.lobby.hub.visibility;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;

public interface IHubVisibilityPolicy
{
    boolean visible(INorthPlayer observer, INorthPlayer target);
}
