package pl.north93.northplatform.api.chat.bukkit.engine;

import org.bukkit.entity.Player;

/**
 * Wynik wysyłania wiadomości przez gracza.
 * @see ChatEngine#sendMessageByPlayer(Player, String)
 */
public enum SendMessageResult
{
    OK,
    NO_PERMISSIONS,
    NO_ROOM
}