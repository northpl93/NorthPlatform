package pl.north93.zgame.datashare.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.SkipInjections;
import pl.north93.zgame.api.global.serializer.platform.annotations.NorthField;
import pl.north93.zgame.datashare.api.cfg.AnnouncerConfig;
import pl.north93.zgame.datashare.api.cfg.DataSharingGroupConfig;

@SkipInjections
public final class DataSharingGroup
{
    private String          name;
    private Boolean         shareChat;
    private AnnouncerConfig announcer;
    @NorthField(type = ArrayList.class)
    private List<String>    dataUnits;

    public DataSharingGroup()
    {
    }

    public DataSharingGroup(final String name, final Boolean shareChat, final AnnouncerConfig announcer, final List<String> dataUnits)
    {
        this.name = name;
        this.shareChat = shareChat;
        this.announcer = announcer;
        this.dataUnits = dataUnits;
    }

    public DataSharingGroup(final DataSharingGroupConfig config)
    {
        this(config.getName(), config.isShareChat(), config.getAnnouncer(), config.getDataUnits());
    }

    public String getName()
    {
        return this.name;
    }

    public Boolean getShareChat()
    {
        return this.shareChat;
    }

    public AnnouncerConfig getAnnouncer()
    {
        return this.announcer;
    }

    public List<String> getDataUnits()
    {
        return this.dataUnits;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("shareChat", this.shareChat).append("announcer", this.announcer).append("dataUnits", this.dataUnits).toString();
    }
}
