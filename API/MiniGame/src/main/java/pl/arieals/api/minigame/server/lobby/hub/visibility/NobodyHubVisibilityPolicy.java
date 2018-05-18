package pl.arieals.api.minigame.server.lobby.hub.visibility;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;

public class NobodyHubVisibilityPolicy implements IHubVisibilityPolicy
{
    public static final IHubVisibilityPolicy INSTANCE = new NobodyHubVisibilityPolicy();

    @Override
    public boolean visible(final INorthPlayer observer, final INorthPlayer target)
    {
        return false;
    }
}
