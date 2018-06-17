package pl.arieals.api.minigame.shared.api.arena;

import pl.north93.zgame.api.global.metadata.MetaKey;

public interface StandardArenaMetaData
{
    /**
     * Identyfikator mapy z configu.
     */
    MetaKey WORLD_ID = MetaKey.get("worldId");

    /**
     * Nazwa wyświetlana mapy z configu.
     */
    MetaKey WORLD_NAME = MetaKey.get("worldName");

    /**
     * Liczba graczy zapisanych do gry dynamicznej na tej arenie.
     */
    MetaKey SIGNED_PLAYERS = MetaKey.get("signedPlayers");
}
