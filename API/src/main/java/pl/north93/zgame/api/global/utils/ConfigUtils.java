package pl.north93.zgame.api.global.utils;

import javax.xml.bind.JAXB;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.exceptions.ConfigurationException;

public final class ConfigUtils
{
    public static <T> T loadConfig(final Class<T> configClass, final String configName)
    {
        //final ClassLoader classLoader = ClassUtils.getCallerClass().getClassLoader();
        final ClassLoader classLoader = configClass.getClassLoader();
        return loadConfig(classLoader, configClass, API.getApiCore().getFile(configName));
    }

    public static <T> T loadConfig(final Class<T> configClass, final File configFile)
    {
        //final ClassLoader classLoader = ClassUtils.getCallerClass().getClassLoader();
        final ClassLoader classLoader = configClass.getClassLoader();
        return loadConfig(classLoader, configClass, configFile);
    }

    private static <T> T loadConfig(final ClassLoader loader, final Class<T> configClass, final File configFile)
    {
        final Logger logger = API.getApiCore().getLogger();
        if (configFile.exists())
        {
            logger.log(Level.INFO, "Loading config from file {0}", configFile);
            return JAXB.unmarshal(configFile, configClass);
        }

        final String configName = configFile.getName();
        final URL exampleConfig = loader.getResource("examples/" + configName);
        if (exampleConfig == null)
        {
            System.out.println(loader);
            System.out.println("examples/" + configName);
            throw new ConfigurationException("Not found config and it's example definition " + configName);
        }

        try
        {
            FileUtils.copyURLToFile(exampleConfig, configFile);
        }
        catch (final IOException e)
        {
            throw new ConfigurationException("Failed to copy default config " + configName, e);
        }

        logger.log(Level.INFO, "Successfully copied default config {0}!", configName);
        return JAXB.unmarshal(exampleConfig, configClass);
    }
}
