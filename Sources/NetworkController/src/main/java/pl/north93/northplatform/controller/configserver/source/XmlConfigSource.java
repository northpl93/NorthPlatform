package pl.north93.northplatform.controller.configserver.source;

import static pl.north93.northplatform.api.global.utils.ConfigUtils.loadConfig;


import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class XmlConfigSource<T> implements IConfigSource<T>
{
    private final Class<T> type;
    private final File     file;

    public XmlConfigSource(final Class<T> type, final File file)
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
        return loadConfig(this.type, this.file);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", this.type).append("file", this.file).toString();
    }
}
