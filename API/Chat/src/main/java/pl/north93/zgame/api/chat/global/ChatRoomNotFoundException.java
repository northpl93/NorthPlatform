package pl.north93.zgame.api.chat.global;

import static java.text.MessageFormat.format;

/**
 * Wyjątek rzucany gdy nie udało się odnaleźć pokoju czatu o podanym ID.
 * <p>
 * Może także pojawić się gdy już posiadasz instancję {@link ChatRoom}, ale
 * inny serwer lub wątek usunął dany pokój.
 */
public class ChatRoomNotFoundException extends RuntimeException
{
    public ChatRoomNotFoundException(final String roomId)
    {
        super(format("Chat room with ID {0} not found", roomId));
    }
}
