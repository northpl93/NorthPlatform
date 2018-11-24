package pl.north93.northplatform.api.minigame.server.lobby.hub.visibility;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

public class DefaultHubVisibilityPolicy implements IHubVisibilityPolicy
{
    public static final IHubVisibilityPolicy INSTANCE = new DefaultHubVisibilityPolicy();

    @Override
    public boolean visible(final INorthPlayer observer, final INorthPlayer target)
    {
        return true;
    }
}
