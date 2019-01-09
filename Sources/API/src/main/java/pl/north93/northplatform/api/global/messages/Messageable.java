package pl.north93.northplatform.api.global.messages;

import java.text.MessageFormat;
import java.util.Locale;

import net.md_5.bungee.api.chat.BaseComponent;

public interface Messageable
{
    /**
     * Zwraca język używany przez ten obiekt (np.gracza)
     *
     * @return język.
     */
    Locale getMyLocale();

    /**
     * Wysyła surową, nietłumaczalną wiadomość do gracza ostylowaną przez dany MessageLayout.
     *
     * @param message surowa treść wiadomości.
     * @param layout Wygląd wiadomości.
     */
    void sendMessage(String message, MessageLayout layout);

    /**
     * Wysyła surową, nietłumaczalną wiadomość do gracza ostylowaną przez domyślny MessageLayout.
     *
     * @param message surowa treść wiadomości.
     */
    default void sendMessage(final String message)
    {
        this.sendMessage(message, MessageLayout.DEFAULT);
    }

    /**
     * Wysyła do gracza komponent zawierający wiadomość ostylowany przez dany MessageLayout.
     *
     * @param component komponent wiadomości.
     * @param layout Wygląd wiadomości.
     */
    void sendMessage(BaseComponent component, MessageLayout layout);

    default void sendMessage(final BaseComponent component)
    {
        this.sendMessage(component, MessageLayout.DEFAULT);
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
     * Wysyła przetłumaczoną wiadomość do gracza ostylowaną przez dany MessageLayout.
     * Obsługuje parametry według MessageFormat.
     *
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param key nazwa klucza wiadomości.
     * @param layout Wygląd wiadomości.
     * @param params parametry.
     */
    default void sendMessage(final MessagesBox messagesBox, final String key, final MessageLayout layout, final Object... params)
    {
        this.sendMessage(messagesBox.getString(this.getMyLocale(), key, params), layout);
    }

    /**
     * Wysyła przetłumaczoną wiadomość do gracza ostylowaną przez domyślny MessageLayout.
     * Obsługuje parametry według MessageFormat.
     *
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param key nazwa klucza wiadomości.
     * @param params parametry.
     */
    default void sendMessage(final MessagesBox messagesBox, final String key, final Object... params)
    {
        this.sendMessage(messagesBox, key, MessageLayout.DEFAULT, params);
    }
}
