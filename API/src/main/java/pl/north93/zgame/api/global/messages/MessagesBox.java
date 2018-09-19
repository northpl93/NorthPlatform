package pl.north93.zgame.api.global.messages;

import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.translateAlternateColorCodes;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.bukkit.utils.chat.LegacyTextParser;

@ToString(of = "fileName")
@EqualsAndHashCode(callSuper = false)
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
            return null;
        }
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
        ParametersEvaluator.evalStringParameters(locale, args);
        String message = getMessageForKey(locale, key);
        return LegacyMessage.fromString(MessageFormat.format(message, args));
    }
    
    public LegacyMessage getLegacy(String locale, String key, Object... args)
    {
        return getLegacy(Locale.forLanguageTag(locale), key, args);
    }
    
    public BaseComponent getComponent(Locale locale, String key, Object... args)
    {
        ParametersEvaluator.evalComponentParameters(locale, args);
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
