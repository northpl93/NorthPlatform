package pl.north93.northplatform.api.minigame.server.lobby.hub.visibility;

import pl.north93.northplatform.api.minigame.shared.api.party.IParty;
import pl.north93.northplatform.api.minigame.shared.api.party.IPartyManager;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class PartyHubVisibilityPolicy implements IHubVisibilityPolicy
{
    public static final IHubVisibilityPolicy INSTANCE = new PartyHubVisibilityPolicy();
    @Inject
    private static      IPartyManager        partyManager;

    @Override
    public boolean visible(final INorthPlayer observer, final INorthPlayer target)
    {
        final IParty observerParty = partyManager.getPartyByPlayer(observer.getIdentity());
        if (observerParty == null)
        {
            return false;
        }

        return observerParty.isAdded(target.getUniqueId());
    }
}
