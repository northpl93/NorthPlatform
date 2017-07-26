package pl.north93.zgame.controller.configserver.source;

import java.io.File;

import pl.north93.zgame.api.global.cfg.ConfigUtils;

public class YamlConfigSource<T> implements IConfigSource<T>
{
    private final Class<T> type;
    private final File file;

    public YamlConfigSource(final Class<T> type, final File file)
    {
        this.type = type;
        this.file = file;
    }

    @Override
    public Class<T> getType()
    {
        return this.type;
    }

    @Override
    public T load()
    {
        return ConfigUtils.loadConfigFile(this.type, this.file);
    }
}
