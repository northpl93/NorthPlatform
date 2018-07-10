package pl.north93.zgame.api.global.messages;

import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.translateAlternateColorCodes;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import pl.north93.zgame.api.bukkit.utils.chat.LegacyTextParser;

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
            //e.printStackTrace();
            //if (! locale.toLanguageTag().equals("pl-PL")) // zapobiegamy Stackoverflow gdy faktycznie takiego bundla nie ma
            //{
            //    return this.getBundle(Locale.forLanguageTag("pl-PL"));
            //}
            
            return null;
        }
        //return null;
    }
    
    public String getString(Locale locale, String key, Object... args)
    {
        return getLegacy(locale, key, args).asString();
    }
    
    public String getString(String locale, String key, Object... args)
    {
        return getString(Locale.forLanguageTag(locale), key, args);
    }
    
    public LegacyMessage getLegacy(Locale locale, String key, Object... args)
    {
        evalStringParameters(locale, args);
        String message = getMessageForKey(locale, key);
        return LegacyMessage.fromString(MessageFormat.format(message, args));
    }
    
    public LegacyMessage getLegacy(String locale, String key, Object... args)
    {
        return getLegacy(Locale.forLanguageTag(locale), key, args);
    }
    
    public BaseComponent getComponent(Locale locale, String key, Object... args)
    {
        evalComponentParameters(locale, args);
        String message = getMessageForKey(locale, key);
        return LegacyTextParser.parseLegacyText(message, args);
    }
    
    public BaseComponent getComponent(String locale, String key, Object... args)
    {
        return getComponent(Locale.forLanguageTag(locale), key, args);
    }

    private String getMessageForKey(Locale locale, String key)
    {
        ResourceBundle bundle = this.getBundle(locale);
        if ( bundle != null && bundle.containsKey(key) )
        {
            return translateAlternateColorCodes(bundle.getString(key));
        }
        else
        {
            return "[" + locale.getLanguage() + ": " + fileName + "#" + key + "]";
        }
    }
    
    private void evalStringParameters(Locale locale, Object[] args)
    {
        for ( int i = 0; i < args.length; i++ )
        {
            if ( args[i] instanceof TranslatableString )
            {
                final TranslatableString translatableString = (TranslatableString) args[i];
                args[i] = translatableString.getValue(locale).toLegacyText();
            }
            else if ( args[i] instanceof BaseComponent )
            {
                final BaseComponent component = (BaseComponent) args[i];
                args[i] = component.toLegacyText();
            }
        }
    }
    
    private void evalComponentParameters(Locale locale, Object[] args)
    {
        for ( int i = 0; i < args.length; i++ )
        {
            if ( args[i] instanceof TranslatableString )
            {
                final TranslatableString translatableString = (TranslatableString) args[i];
                args[i] = translatableString.getValue(locale);
            }
            else if (! (args[i] instanceof BaseComponent))
            {
                final String possibleLegacyText = String.valueOf(args[i]);
                args[i] = translateAlternateColorCodes(possibleLegacyText);
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
    
    // some legacy shit and deprecated methods
    
    // BELOW ARE LEGACY METHODS ONLY FOR BACKWARD COMAPTIBILITY
    // you never should use that methods in new code
    
    @Deprecated
    public String getMessage(final Locale locale, final String key)
    {
        return getMessageForKey(locale, key);
    }

    @Deprecated
    public String getMessage(final String locale, final String key)
    {
        return this.getMessage(Locale.forLanguageTag(locale), key);
    }

    @Deprecated // uniemozliwia tlumaczenie wiadomosci per-gracz
    public String getMessage(final String key)
    {
        return this.getMessage(Locale.forLanguageTag("pl-PL"), key);
    }

    // = = = POBIERANIE WIADOMOSCI Z PARAMETRAMI = = = //

    @Deprecated
    public BaseComponent getMessage(final Locale locale, final String key, final Object... params)
    {
        return getComponent(locale, key, params);
    }

    @Deprecated
    public String getLegacyMessage(final Locale locale, final String key, final Object... params)
    {
        return getLegacy(locale, key, params).asString();
    }

    @Deprecated
    public BaseComponent getMessage(final String locale, final String key, final Object... params)
    {
        return this.getMessage(Locale.forLanguageTag(locale), key, params);
    }

    @Deprecated
    public String getLegacyMessage(final String locale, final String key, final Object... params)
    {
        return this.getLegacyMessage(Locale.forLanguageTag(locale), key, params);
    }

    // = = = WYSYLANIE WIADOMOSCI = = = //

    @Deprecated
    public void sendMessage(final Messageable messageable, final String key, final MessageLayout layout, final Object... params)
    {
        messageable.sendMessage(this, key, layout, params);
    }

    @Deprecated
    public void sendMessage(final Messageable messageable, final String key, final Object... params)
    {
        this.sendMessage(messageable, key, MessageLayout.DEFAULT, params);
    }

    // nie powinno jebnac na innych platformach niz Bukkit o ile nie wykonamy tej metody
    @Deprecated
    public void sendMessage(final org.bukkit.entity.Player player, final String key, final MessageLayout layout, final Object... params)
    {
        final BaseComponent message = this.getMessage(player.getLocale(), key, (Object[]) params);
        player.sendMessage(layout.processMessage(message));
    }

    // nie powinno jebnac na innych platformach niz Bukkit o ile nie wykonamy tej metody
    @Deprecated
    public void sendMessage(final org.bukkit.entity.Player player, final String key, final Object... params)
    {
        this.sendMessage(player, key, MessageLayout.DEFAULT, params);
    }

    // nie powinno jebnac na innych platformach niz Bungee o ile nie wykonamy tej metody
    @Deprecated
    public void sendMessage(final ProxiedPlayer player, final String key, final MessageLayout layout, final Object... params)
    {
        final BaseComponent message = this.getMessage(player.getLocale(), key, (Object[]) params);
        player.sendMessage(layout.processMessage(message));
    }

    // nie powinno jebnac na innych platformach niz Bungee o ile nie wykonamy tej metody
    @Deprecated
    public void sendMessage(final ProxiedPlayer player, final String key, final Object... params)
    {
        this.sendMessage(player, key, MessageLayout.DEFAULT, params);
    }
}
