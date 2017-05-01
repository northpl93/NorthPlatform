package pl.north93.zgame.api.global.messages;

import java.text.MessageFormat;
import java.util.Locale;

public interface Messageable
{
    /**
     * Zwraca język używany przez ten obiekt (np.gracza)
     * @return język.
     */
    Locale getLocale();

    void sendMessage(String message, boolean colorText);

    @Deprecated
    default void sendMessage(String message)
    {
        this.sendMessage(message, true);
    }

    @Deprecated
    default void sendMessage(final String message, final Object... params)
    {
        this.sendMessage(MessageFormat.format(message, params));
    }

    default void sendMessage(final MessagesBox messagesBox, final String key)
    {
        this.sendMessage(messagesBox.getMessage(this.getLocale(), key));
    }

    default void sendMessage(final MessagesBox messagesBox, final String key, final Object... params)
    {
        this.sendMessage(messagesBox.getMessage(this.getLocale(), key), params);
    }

    default void sendMessageColor(final MessagesBox messagesBox, final String key, final boolean colorText, final Object... params)
    {
        this.sendMessage(MessageFormat.format(messagesBox.getMessage(this.getLocale(), key), params), colorText);
    }
}
