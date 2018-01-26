package pl.arieals.api.minigame.shared.api.party;

import java.util.UUID;

import pl.arieals.api.minigame.shared.api.location.INetworkLocation;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.players.Identity;

public interface IPartyManager
{
    /**
     * Zwraca Party do którego należy dany gracz.
     *
     * @param identity Identity danego gracza.
     * @return Party do którego nalezy gracz.
     */
    IParty getPartyByPlayer(Identity identity) throws PlayerNotFoundException;

    /**
     * Tworzy nowe party z podanym właścicielem i daną lokalizacją sieciową.
     *
     * @param ownerIdentity Identity właściciela party.
     * @param location Lokalizacja sieciowa party.
     * @return Nowa instancja party o podanym właścicielu i lokalizacji.
     */
    IParty createParty(Identity ownerIdentity, INetworkLocation location) throws PlayerNotFoundException, PlayerAlreadyHasPartyException;

    void changePartyOwner(UUID partyId, Identity newOwnerIdentity, INetworkLocation location);

    default void changePartyOwner(IParty party, Identity newOwnerIdentity, INetworkLocation location)
    {
        this.changePartyOwner(party.getId(), newOwnerIdentity, location);
    }

    void changePartyLocation(UUID partyId, INetworkLocation location);

    /**
     * Tworzy u podanego gracza zaproszenie do danego party.
     *
     * @param partyId
     * @param playerIdentity
     * @throws PlayerNotFoundException
     * @throws PlayerAlreadyHasPartyException
     */
    void invitePlayer(UUID partyId, Identity playerIdentity) throws PlayerNotFoundException, PlayerAlreadyHasPartyException;

    /**
     * Zwraca zaproszenie które aktualnie posiada gracz.
     *
     * @param playerIdentity
     * @return
     * @throws PlayerNotFoundException
     */
    PartyInvite getInvite(Identity playerIdentity) throws PlayerNotFoundException;

    void addPlayerToParty(UUID partyId, Identity playerIdentity) throws PlayerNotFoundException, PlayerAlreadyHasPartyException;

    void deleteParty(UUID partyId);
}
