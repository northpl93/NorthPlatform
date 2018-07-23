package pl.arieals.api.minigame.shared.api.party;

import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Interfejs udostępiniający dane Party w trybie tylko do odczytu.
 * Aby uzyskać dostęp do atomowej edycji danych należy użyć {@link IPartyAccess}.
 */
public interface IParty
{
    UUID getId();

    Identity getOwner();

    default boolean isOwner(final UUID playerId)
    {
        return this.getOwner().getUuid().equals(playerId);
    }

    Set<PartyInvite> getInvites();

    boolean isInvited(UUID playerId);

    Set<Identity> getPlayers();

    /**
     * Pomocnicza metoda konwertująca wszystkich graczy w party na
     * odpowiadające im {@link PlayerJoinInfo}.
     *
     * @return Lista obiektów PlayerJoinInfo wszystkich graczy z party.
     */
    Set<PlayerJoinInfo> getJoinInfos();

    boolean isAdded(UUID playerId);

    boolean isAddedOrInvited(UUID playerId);

    /**
     * Zwraca aktualną lokację w której znajduje się kapitan party.
     *
     * @return Aktualna lokalizacja kapitana party.
     */
    IPlayerStatus getTargetLocation();

    /**
     * Zwraca prawdę jeśli właściciel party jest w grze.
     *
     * @return Prawda jeśli właściciel party jest w grze.
     */
    default boolean isInGame()
    {
        return this.getTargetLocation().getType() == IPlayerStatus.StatusType.GAME;
    }
}
