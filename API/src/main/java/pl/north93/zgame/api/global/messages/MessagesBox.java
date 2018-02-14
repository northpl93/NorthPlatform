package pl.north93.zgame.api.global.messages;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.bukkit.utils.ChatUtils;

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
            if (! locale.toLanguageTag().equals("pl-PL")) // zapobiegamy Stackoverflow gdy faktycznie takiego bundla nie ma
            {
                return this.getBundle(Locale.forLanguageTag("pl-PL"));
            }
        }
        return null;
    }

    public String getMessage(final Locale locale, final String key)
    {
        ResourceBundle bundle = this.getBundle(locale);
        if ( bundle.containsKey(key) )
        {
            return ChatUtils.translateAlternateColorCodes(bundle.getString(key));
        }
        else
        {
            return "[" + locale.getLanguage() + ": " + fileName + "." + key + "]";
        }
    }

    public String getMessage(final String locale, final String key)
    {
        return this.getMessage(Locale.forLanguageTag(locale), key);
    }

    public String getMessage(final Locale locale, final String key, final Object... params)
    {
        evalTranslatableString(locale, params);
        return MessageFormat.format(this.getMessage(locale, key), (Object[]) params);
    }

    public String getMessage(final String locale, final String key, final Object... params)
    {
        evalTranslatableString(Locale.forLanguageTag(locale), params);
        return MessageFormat.format(this.getMessage(locale, key), (Object[]) params);
    }

    @Deprecated // uniemozliwia tlumaczenie wiadomosci per-gracz
    public String getMessage(final String key)
    {
        return this.getMessage(Locale.forLanguageTag("pl-PL"), key);
    }

    // = = = WYSYLANIE WIADOMOSCI = = = //

    public void sendMessage(final Messageable messageable, final String key, final MessageLayout layout, final Object... params)
    {
        final String message = this.getMessage(messageable.getMyLocale(), key, (Object[]) params);
        for (final String line : layout.processMessage(message))
        {
            messageable.sendMessage(line, true);
        }
    }

    public void sendMessage(final Messageable messageable, final String key, final Object... params)
    {
        this.sendMessage(messageable, key, MessageLayout.DEFAULT, params);
    }

    // nie powinno jebnac na innych platformach niz Bukkit o ile nie wykonamy tej metody
    public void sendMessage(final org.bukkit.entity.Player player, final String key, final MessageLayout layout, final Object... params)
    {
        final String message = this.getMessage(player.getLocale(), key, (Object[]) params);
        player.sendMessage(layout.processMessage(message));
    }

    // nie powinno jebnac na innych platformach niz Bukkit o ile nie wykonamy tej metody
    public void sendMessage(final org.bukkit.entity.Player player, final String key, final Object... params)
    {
        this.sendMessage(player, key, MessageLayout.DEFAULT, params);
    }

    // nie powinno jebnac na innych platformach niz Bungee o ile nie wykonamy tej metody
    public void sendMessage(final ProxiedPlayer player, final String key, final MessageLayout layout, final Object... params)
    {
        final String message = this.getMessage(player.getLocale(), key, (Object[]) params);
        for (final String messageLine : layout.processMessage(message))
        {
            player.sendMessage(TextComponent.fromLegacyText(messageLine));
        }
    }

    // nie powinno jebnac na innych platformach niz Bungee o ile nie wykonamy tej metody
    public void sendMessage(final ProxiedPlayer player, final String key, final Object... params)
    {
        this.sendMessage(player, key, MessageLayout.DEFAULT, params);
    }

    private void evalTranslatableString(Locale locale, Object[] args)
    {
        for ( int i = 0; i < args.length; i++ )
        {
            if ( args[i] instanceof TranslatableString )
            {
                args[i] = ((TranslatableString) args[i]).getValue(locale);
            }
        }
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("fileName", this.fileName).toString();
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(loader, fileName);
    }
    
    @Override
    public boolean equals(Object obj)
    {
         if ( this == obj )
         {
             return true;
         }
         if ( obj == null || obj.getClass() != this.getClass() )
         {
             return false;
         }
         
         MessagesBox other = (MessagesBox) obj;
         return Objects.equals(other.loader, this.loader) && Objects.equals(other.fileName, this.fileName);
    }

    @Deprecated // weird utility, uniemozliwa tlumaczenie wiadomosci per gracz
    public static String message(final MessagesBox messagesBox, final String key, final Object... params)
    {
        return MessageFormat.format(messagesBox.getMessage(key).replace('&', (char)167), params);
    }
}
