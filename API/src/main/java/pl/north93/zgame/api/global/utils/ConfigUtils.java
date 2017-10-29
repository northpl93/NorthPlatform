package pl.north93.zgame.api.global.utils;

import java.io.File;
import java.io.IOException;

import org.diorite.cfg.system.Template;
import org.diorite.cfg.system.TemplateCreator;
import org.diorite.utils.DioriteUtils;

public class ConfigUtils
{
    public static <T> T loadConfigFile(final Class<T> clazz, final File f)
    {
        T config;

        final Template<T> cfgTemp = TemplateCreator.getTemplate(clazz);
        if (f.exists())
        {
            try
            {
                config = cfgTemp.load(f);
                if (config == null)
                {
                    config = cfgTemp.fillDefaults(clazz.newInstance());
                }
            }
            catch (final IOException e)
            {
                throw new RuntimeException("IO exception when loading config file: " + f, e);
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                throw new RuntimeException("Reflection exception when loading config file: " + f, e);
            }
        }
        else
        {
            try
            {
                config = cfgTemp.fillDefaults(clazz.newInstance());
                DioriteUtils.createFile(f);
                cfgTemp.dump(f, config, false);
            }
            catch (final IOException | InstantiationException | IllegalAccessException e)
            {
                throw new RuntimeException("Can't create configuration file!", e);
            }
        }

        return config;
    }
}
