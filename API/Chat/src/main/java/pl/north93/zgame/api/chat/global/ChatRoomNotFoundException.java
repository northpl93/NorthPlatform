package pl.north93.zgame.api.chat.global;

import static java.text.MessageFormat.format;

public class ChatRoomNotFoundException extends RuntimeException
{
    public ChatRoomNotFoundException(final String roomId)
    {
        super(format("Chat room with ID {0} not found", roomId));
    }
}
