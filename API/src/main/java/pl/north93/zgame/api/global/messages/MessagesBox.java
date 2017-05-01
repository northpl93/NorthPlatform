package pl.north93.zgame.api.global.messages;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MessagesBox
{
    private static final UTF8Control CONTROL = new UTF8Control();
    private final ClassLoader loader;
    private final String      fileName;

    public MessagesBox(final ClassLoader loader, final String fileName)
    {
        this.loader = loader;
        this.fileName = fileName;
    }

    public ResourceBundle getBundle(final Locale locale)
    {
        try
        {
            return ResourceBundle.getBundle(this.fileName, locale, this.loader, CONTROL);
        }
        catch (final MissingResourceException e)
        {
            e.printStackTrace();
            return this.getBundle(Locale.forLanguageTag("pl-PL"));
        }
    }

    public String getMessage(final Locale locale, final String key)
    {
        return this.getBundle(locale).getString(key);
    }

    public String getMessage(final String locale, final String key)
    {
        return this.getMessage(Locale.forLanguageTag(locale), key);
    }

    @Deprecated // uniemozliwia tlumaczenie wiadomosci per-gracz
    public String getMessage(final String key)
    {
        return this.getMessage(Locale.forLanguageTag("pl-PL"), key);
    }

    public void sendMessage(final Messageable messageable, final String key, final String... params)
    {
        messageable.sendMessage(this.getMessage(messageable.getLocale(), key), (Object[]) params);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("fileName", this.fileName).toString();
    }

    @Deprecated // weird utility, uniemozliwa tlumaczenie wiadomosci per gracz
    public static String message(final MessagesBox messagesBox, final String key, final Object... params)
    {
        return MessageFormat.format(messagesBox.getMessage(key).replace('&', (char)167), params);
    }
}
