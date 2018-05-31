package pl.arieals.api.minigame.shared.api.party;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.players.IPlayer;
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
     * Zwraca wszystkie utworzone party w sieci.
     *
     * @return Niemodyfikowalna lista wszystkich party w sieci.
     */
    Collection<IParty> getAllParties();

    /**
     * Tworzy nowe party z podanym właścicielem i daną lokalizacją sieciową.
     *
     * @param ownerIdentity Identity właściciela party.
     * @param location Lokalizacja sieciowa party.
     * @return Nowa instancja party o podanym właścicielu i lokalizacji.
     */
    IParty createParty(Identity ownerIdentity, IPlayerStatus location) throws PlayerNotFoundException, PlayerAlreadyHasPartyException;

    <T> T access(UUID partyId, Function<IPartyAccess, T> atomicFunction);

    void access(UUID partyId, Consumer<IPartyAccess> atomicFunction);

    void access(IPlayer player, Consumer<IPartyAccess> atomicFunction);

    <T> T access(IPlayer player, Function<IPartyAccess, T> atomicFunction);

    /**
     * Zwraca ostatnie zaproszenie które otrzymał gracz i jest aktualne.
     * Gdy gracz nie posiada zaproszenia zostanie zwrócony null.
     *
     * @param playerIdentity Identity dla którego pobieramy zaproszenie.
     * @return Ostatnie zaproszenie danego gracza lub null.
     * @throws PlayerNotFoundException Gdy nie udało się powiązać Identity z graczem.
     */
    PartyInvite getLatestInvite(Identity playerIdentity) throws PlayerNotFoundException;
}
