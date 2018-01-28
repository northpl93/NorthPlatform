package pl.arieals.api.minigame.shared.api.party;

import pl.arieals.api.minigame.shared.api.location.INetworkLocation;
import pl.arieals.api.minigame.shared.api.party.event.InviteToPartyNetEvent;
import pl.arieals.api.minigame.shared.api.party.event.JoinPartyNetEvent;
import pl.arieals.api.minigame.shared.api.party.event.LeavePartyNetEvent.LeavePartyReason;
import pl.arieals.api.minigame.shared.api.party.event.LocationChangePartyNetEvent;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Interfejs udostępniający metody modyfikujące Party.
 * Aby uzyskać ten dostęp należy skorzystać z metod access w {@link IPartyManager}.
 */
public interface IPartyAccess extends IParty
{
    /**
     * Zmienia lokalizację w której znajduje się lider Party (a więc i także reszta graczy).
     * Wywołuje także sieciowy event {@link LocationChangePartyNetEvent}.
     *
     * @param location Nowa lokalizacja tego Party.
     */
    void changeLocation(INetworkLocation location);

    /**
     * Zmienia lidera/właściciela tego Party.
     * Aby wykonanie tej metody się powiodło gracz musi być już dodany do Party.
     *
     * @param newOwnerIdentity Identity gracza którego ustanawiamy liderem party.
     * @return True jak się uda (gracz jest w Party), false jak nie.
     * @throws PlayerNotFoundException Gdy nie można powiązać danego Identity z graczem.
     */
    boolean changeOwner(Identity newOwnerIdentity) throws PlayerNotFoundException;

    /**
     * Tworzy zaproszenie do party dla danego gracza i przypisuje mu je jako ostatnio otrzymane.
     * Wysyła także event sieciowy {@link InviteToPartyNetEvent}.
     *
     * @param playerIdentity Identity gracza którego zapraszamy.
     * @throws PlayerNotFoundException Gdy nie udało się powiązać Identity z graczem.
     * @throws PlayerAlreadyHasPartyException Gdy dany gracz jest już w innym Party.
     */
    void invitePlayer(Identity playerIdentity) throws PlayerNotFoundException, PlayerAlreadyHasPartyException;

    /**
     * Dodaje podanego gracza do tego Party.
     * Usuwa z listy jego zaproszenie i usuwa ostatnie zaproszenie z obiektu gracza.
     * Jednocześnie należy mieć na uwadze, że ta metoda nie wymaga posiadania zaproszenia.
     * Wysyła także event sieciowy {@link JoinPartyNetEvent}.
     *
     * @param playerIdentity Gracz którego dodajemy do Party. (nie musi mieć zaproszenia)
     * @throws PlayerNotFoundException Gdy nie udało się powiązać Identity z graczem.
     * @throws PlayerAlreadyHasPartyException Gdy dany gracz jest już w innym Party.
     */
    void addPlayer(Identity playerIdentity) throws PlayerNotFoundException, PlayerAlreadyHasPartyException;

    void removePlayer(Identity playerIdentity, LeavePartyReason reason) throws PlayerNotFoundException;

    void delete();
}
