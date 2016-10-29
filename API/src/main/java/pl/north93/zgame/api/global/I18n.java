package pl.north93.zgame.api.global;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class I18n
{
    private static final ResourceBundle messages = ResourceBundle.getBundle("Messages", new Locale("pl", "PL"), new UTF8Control());

    public static ResourceBundle getMessages()
    {
        return messages;
    }

    public static BaseComponent[] getMessage(final String key)
    {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', messages.getString(key)));
    }

    public static BaseComponent[] getMessage(final String key, final Object... values)
    {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', MessageFormat.format(messages.getString(key), values)));
    }

    public static String getBukkitMessage(final String key)
    {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(key));
    }

    public static String getBukkitMessage(final String key, final Object... values)
    {
        return ChatColor.translateAlternateColorCodes('&', MessageFormat.format(messages.getString(key), values));
    }

    private static class UTF8Control extends ResourceBundle.Control
    {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException
        {
            final String bundleName = this.toBundleName(baseName, locale);
            final String resourceName = this.toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload)
            {
                final URL url = loader.getResource(resourceName);
                if (url != null)
                {
                    final URLConnection connection = url.openConnection();
                    if (connection != null)
                    {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            }
            else
            {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null)
            {
                try
                {
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                }
                finally
                {
                    stream.close();
                }
            }
            return bundle;
        }
    }
}
