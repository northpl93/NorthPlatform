package pl.north93.zgame.api.global.update.api;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class UpdateFile
{
    private String name;
    private String md5;
    private String download;

    public UpdateFile()
    {
    }

    public UpdateFile(final String name, final String md5, final String download)
    {
        this.name = name;
        this.md5 = md5;
        this.download = download;
    }

    public String getName()
    {
        return this.name;
    }

    public String getMd5()
    {
        return this.md5;
    }

    public String getDownload()
    {
        return this.download;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("md5", this.md5).append("download", this.download).toString();
    }
}
