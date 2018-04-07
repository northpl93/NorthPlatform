package pl.north93.zgame.api.chat.bukkit.engine;

import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Wynik wysyłania wiadomości przez gracza.
 * @see ChatEngine#sendMessageByPlayer(Player, UUID, String)
 */
public enum SendMessageResult
{
    OK,
    NO_PERMISSIONS,
    NO_ROOM
}