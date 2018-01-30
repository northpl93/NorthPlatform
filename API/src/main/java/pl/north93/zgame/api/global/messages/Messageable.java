package pl.north93.zgame.api.global.messages;

import java.text.MessageFormat;
import java.util.Locale;

public interface Messageable
{
    /**
     * Zwraca język używany przez ten obiekt (np.gracza)
     *
     * @return język.
     */
    Locale getMyLocale();

    /**
     * Wysyła wiadomość do gracza ostylowaną przez dany MessageLayout.
     *
     * @param message treść wiadomości.
     * @param layout Wygląd wiadomości.
     */
    void sendMessage(String message, MessageLayout layout);

    /**
     * Wysyła wiadomość do gracza ostylowaną przez domyślny MessageLayout.
     *
     * @param message treść wiadomości.
     */
    default void sendMessage(String message)
    {
        this.sendMessage(message, MessageLayout.DEFAULT);
    }

    /**
     * Wysyła wiadomość do gracza ostylowaną przez dany MessageLayout.
     * Obsługuje parametry według MessageFormat.
     *
     * @param message treść wiadomości.
     * @param layout Wygląd wiadomości.
     * @param params parametry.
     */
    default void sendMessage(final String message, final MessageLayout layout, final Object... params)
    {
        this.sendMessage(MessageFormat.format(message, params), layout);
    }

    /**
     * Wysyła wiadomość do gracza ostylowaną przez domyślny MessageLayout.
     * Obsługuje parametry według MessageFormat.
     *
     * @param message treść wiadomości.
     * @param params parametry.
     */
    default void sendMessage(final String message, final Object... params)
    {
        this.sendMessage(message, MessageLayout.DEFAULT, params);
    }

    /**
     * Wysyła przetłumaczoną wiadomość do gracza z danym stylem.
     *
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param layout Wygląd wiadomości.
     * @param key nazwa klucza wiadomości.
     */
    default void sendMessage(final MessagesBox messagesBox, final String key, final MessageLayout layout)
    {
        this.sendMessage(messagesBox.getMessage(this.getMyLocale(), key), layout);
    }

    /**
     * Wysyła przetłumaczoną wiadomość do gracza ostylowaną przez domyślny MessageLayout.
     *
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param key nazwa klucza wiadomości.
     */
    default void sendMessage(final MessagesBox messagesBox, final String key)
    {
        this.sendMessage(messagesBox, key, MessageLayout.DEFAULT);
    }

    /**
     * Wysyła przetłumaczoną wiadomość do gracza.
     * Obsługuje parametry według MessageFormat.
     *
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param key nazwa klucza wiadomości.
     * @param params parametry.
     */
    default void sendMessage(final MessagesBox messagesBox, final String key, final MessageLayout layout, final Object... params)
    {
        this.sendMessage(messagesBox.getMessage(this.getMyLocale(), key, params), layout);
    }

    default void sendMessage(final MessagesBox messagesBox, final String key, final Object... params)
    {
        this.sendMessage(messagesBox, key, MessageLayout.DEFAULT, params);
    }
}
