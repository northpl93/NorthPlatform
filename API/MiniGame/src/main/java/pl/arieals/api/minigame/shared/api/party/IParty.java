package pl.arieals.api.minigame.shared.api.party;

import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.location.INetworkLocation;

public interface IParty
{
    UUID getId();

    UUID getOwnerId();

    default boolean isOwner(final UUID playerId)
    {
        return this.getOwnerId().equals(playerId);
    }

    Set<UUID> getPlayers();

    /**
     * Zwraca aktualną lokację w której znajduje się kapitan party.
     *
     * @return Aktualna lokalizacja kapitana party.
     */
    INetworkLocation getTargetLocation();

    /**
     * Zwraca prawdę jeśli właściciel party jest w grze.
     *
     * @return Prawda jeśli właściciel party jest w grze.
     */
    default boolean isInGame()
    {
        return this.getTargetLocation().getType() == INetworkLocation.LocationType.GAME;
    }
}
