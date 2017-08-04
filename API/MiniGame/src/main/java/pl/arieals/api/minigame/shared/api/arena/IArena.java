package pl.arieals.api.minigame.shared.api.arena;

import java.util.List;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.GamePhase;

public interface IArena
{
    UUID getId();

    UUID getServerId();

    String getMiniGameId();

    String getWorldId();

    GamePhase getGamePhase();

    default int getPlayersCount()
    {
        return this.getPlayers().size();
    }

    List<UUID> getPlayers();
}
