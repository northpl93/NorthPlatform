package pl.north93.zgame.api.global.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@FunctionalInterface
public interface Messageable
{
    void sendMessage(String message, boolean colorText);

    default void sendMessage(String message)
    {
        this.sendMessage(message, true);
    }

    default void sendMessage(final String message, final Object... params)
    {
        this.sendMessage(MessageFormat.format(message, params));
    }

    default void sendMessage(final ResourceBundle resourceBundle, final String key)
    {
        this.sendMessage(resourceBundle.getString(key));
    }

    default void sendMessage(final ResourceBundle resourceBundle, final String key, final Object... params)
    {
        this.sendMessage(resourceBundle.getString(key), params);
    }

    default void sendMessageColor(final ResourceBundle resourceBundle, final String key, final boolean colorText, final Object... params)
    {
        this.sendMessage(MessageFormat.format(resourceBundle.getString(key), params), colorText);
    }
}
