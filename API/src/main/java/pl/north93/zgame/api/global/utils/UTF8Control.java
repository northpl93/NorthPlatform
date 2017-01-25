package pl.north93.zgame.api.global.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class UTF8Control extends ResourceBundle.Control
{
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException
    {
        final String bundleName = this.toBundleName(baseName, locale);
        final String resourceName = this.toResourceName(bundleName, "properties");
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
                return new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            }
            finally
            {
                stream.close();
            }
        }
        return null;
    }
}
