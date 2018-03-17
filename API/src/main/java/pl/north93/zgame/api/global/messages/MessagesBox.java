package pl.north93.zgame.api.global.messages;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;

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

    public BaseComponent getMessage(final Locale locale, final String key, final Object... params)
    {
        evalParameters(locale, params);
        final String legacyText = MessageFormat.format(this.getMessage(locale, key), (Object[]) params);
        return ChatUtils.fromLegacyText(legacyText);
    }

    public BaseComponent getMessage(final String locale, final String key, final Object... params)
    {
        evalParameters(Locale.forLanguageTag(locale), params);
        final String legacyText = MessageFormat.format(this.getMessage(locale, key), (Object[]) params);
        return ChatUtils.fromLegacyText(legacyText);
    }

    @Deprecated // uniemozliwia tlumaczenie wiadomosci per-gracz
    public String getMessage(final String key)
    {
        return this.getMessage(Locale.forLanguageTag("pl-PL"), key);
    }

    // = = = WYSYLANIE WIADOMOSCI = = = //

    public void sendMessage(final Messageable messageable, final String key, final MessageLayout layout, final Object... params)
    {
        messageable.sendMessage(this, key, layout, params);
    }

    public void sendMessage(final Messageable messageable, final String key, final Object... params)
    {
        this.sendMessage(messageable, key, MessageLayout.DEFAULT, params);
    }

    // nie powinno jebnac na innych platformach niz Bukkit o ile nie wykonamy tej metody
    public void sendMessage(final org.bukkit.entity.Player player, final String key, final MessageLayout layout, final Object... params)
    {
        final BaseComponent message = this.getMessage(player.getLocale(), key, (Object[]) params);
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
        final BaseComponent message = this.getMessage(player.getLocale(), key, (Object[]) params);
        player.sendMessage(layout.processMessage(message));
    }

    // nie powinno jebnac na innych platformach niz Bungee o ile nie wykonamy tej metody
    public void sendMessage(final ProxiedPlayer player, final String key, final Object... params)
    {
        this.sendMessage(player, key, MessageLayout.DEFAULT, params);
    }

    private void evalParameters(Locale locale, Object[] args)
    {
        for ( int i = 0; i < args.length; i++ )
        {
            // todo ulepszyc obsluge komponentów, aby byly wstawiane bezposrednio a nie konwertowane na legacy text
            if ( args[i] instanceof TranslatableString )
            {
                final TranslatableString translatableString = (TranslatableString) args[i];
                args[i] = translatableString.getValue(locale).toLegacyText();
            }
            else if ( args[i] instanceof BaseComponent )
            {
                args[i] = ((BaseComponent) args[i]).toLegacyText();
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
