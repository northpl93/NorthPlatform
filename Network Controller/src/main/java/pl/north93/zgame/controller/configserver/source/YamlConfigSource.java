package pl.north93.zgame.controller.configserver.source;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.utils.ConfigUtils;

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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", this.type).append("file", this.file).toString();
    }
}
