package pl.north93.northplatform.api.global.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.API;
import pl.north93.northplatform.api.global.utils.exceptions.ConfigurationException;

@Slf4j
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
        if (configFile.exists())
        {
            log.info("Loading config from file {}", configFile);
            return JaxbUtils.unmarshal(configFile, configClass);
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

        log.info("Successfully copied default config {}!", configName);
        return JaxbUtils.unmarshal(exampleConfig, configClass);
    }
}
