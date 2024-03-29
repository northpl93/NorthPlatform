package pl.north93.northplatform.api.minigame.server.gamehost.arena.player;

import java.util.function.Predicate;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.chat.global.ChatRoom;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArenaManager;

public class ArenaChatManager
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox messages;
    private final LocalArena arena;
    private final ChatRoom chatRoom; // pokój czatu
    private final ChatRoom spectatorsRoom; // pokój czatu spectatorów

    public ArenaChatManager(final LocalArenaManager localArenaManager, final LocalArena arena)
    {
        this.arena = arena;

        this.chatRoom = localArenaManager.getChatRoomFor(arena, false);
        this.spectatorsRoom = localArenaManager.getChatRoomFor(arena, true);
    }

    /**
     * Zwraca pokój czatu powiązany z tą areną i przeznaczony dla
     * graczy biorących udział w rozgrywce.
     * @return Pokój czatu przeznaczony dla graczy biorących udział w rozgrywce.
     */
    public ChatRoom getChatRoom()
    {
        return this.chatRoom;
    }

    /**
     * Zwraca pokój czatu powiązany z tą areną i przeznaczony dla
     * graczy oglądających grę.
     * @return Pokój czatu przeznaczony dla graczy oglądających grę.
     */
    public ChatRoom getSpectatorsRoom()
    {
        return this.spectatorsRoom;
    }

    // WYSYŁANIE WIADOMOSCI //

    /**
     * Wysyła przetłumaczoną wiadomość do graczy znajdujących się na tej arenie.
     * Dodatkowo uwzględnia warunek wysłania wiadomości.
     *
     * @param condition warunek który musi spełnić gracz aby otrzymać wiadomość.
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param messageKey klucz wiadomości.
     * @param layout Wyglad tej wiadomości.
     * @param args argumenty.
     */
    public void broadcast(final Predicate<INorthPlayer> condition, final MessagesBox messagesBox, final String messageKey, final MessageLayout layout, final Object... args)
    {
        for (final INorthPlayer player : this.arena.getPlayersManager().getAllPlayers())
        {
            if (! condition.test(player))
            {
                continue;
            }

            player.sendMessage(messagesBox, messageKey, layout, args);
        }
    }

    /**
     * Wysyła przetłumaczoną wiadomość do graczy znajdujących się na tej arenie
     * z domyślnym layoutem.
     * Dodatkowo uwzględnia warunek wysłania wiadomości.
     *
     * @param condition warunek który musi spełnić gracz aby otrzymać wiadomość.
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param messageKey klucz wiadomości.
     * @param args argumenty.
     */
    public void broadcast(final Predicate<INorthPlayer> condition, final MessagesBox messagesBox, final String messageKey, final Object... args)
    {
        this.broadcast(condition, messagesBox, messageKey, MessageLayout.DEFAULT, args);
    }

    /**
     * Wysyła przetłumaczoną wiadomość do graczy znajdujących się na tej arenie.
     *
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param messageKey klucz wiadomości.
     * @param layout Wyglad tej wiadomości.
     * @param args argumenty.
     */
    public void broadcast(final MessagesBox messagesBox, final String messageKey, final MessageLayout layout, final Object... args)
    {
        for (final INorthPlayer player : this.arena.getPlayersManager().getAllPlayers())
        {
            player.sendMessage(messagesBox, messageKey, layout, args);
        }
    }

    /**
     * Wysyła przetłumaczoną wiadomość do graczy znajdujących się na tej arenie
     * z domyślnym layoutem.
     *
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param messageKey klucz wiadomości.
     * @param args argumenty.
     */
    public void broadcast(final MessagesBox messagesBox, final String messageKey, final Object... args)
    {
        this.broadcast(messagesBox, messageKey, MessageLayout.DEFAULT, args);
    }

    /*default*/ void announceJoinLeft(final Player player, final String messageKey)
    {
        final String name = player.getName();
        final int playersCount = this.arena.getPlayersCount();
        final int maxPlayers = this.arena.getPlayersManager().getMaxPlayers();

        this.broadcast(this.messages, messageKey, name, playersCount, maxPlayers);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("chatRoom", this.chatRoom).append("spectatorsRoom", this.spectatorsRoom).toString();
    }
}
