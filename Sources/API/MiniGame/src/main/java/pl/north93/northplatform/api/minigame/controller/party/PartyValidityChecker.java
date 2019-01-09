package pl.north93.northplatform.api.minigame.controller.party;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.minigame.shared.api.party.IParty;
import pl.north93.northplatform.api.minigame.shared.api.party.IPartyAccess;
import pl.north93.northplatform.api.minigame.shared.api.party.IPartyManager;
import pl.north93.northplatform.api.minigame.shared.api.party.PartyInvite;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

@Slf4j
public class PartyValidityChecker implements Runnable
{
    private static final int           TIME = 60 * 20; // 1 minute
    private final        IPartyManager partyManager;

    @Bean
    private PartyValidityChecker(final IPartyManager partyManager, final ApiCore apiCore)
    {
        this.partyManager = partyManager;
        apiCore.getPlatformConnector().runTaskAsynchronously(this, TIME);
    }

    @Override
    public void run()
    {
        for (final IParty party : this.partyManager.getAllParties())
        {
            if (this.isPartyValid(party))
            {
                continue;
            }

            log.info("Deleting party {} because not valid!", party.getId());
            this.partyManager.access(party.getId(), IPartyAccess::delete);
        }
    }

    private boolean isPartyValid(final IParty party)
    {
        if (party.getPlayers().size() > 1)
        {
            // mamy w party kogos wiecej niz sam wlasciciel
            return true;
        }

        for (final PartyInvite invite : party.getInvites())
        {
            if (invite.isStillValid())
            {
                // mamy przynajmniej jedno wazne zaproszenie
                return true;
            }
        }

        // mamy samego wlasciciela i brak waznych zaproszen
        return false;
    }
}
