package pl.north93.zgame.datashare.api;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.SkipInjections;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.ArrayListTemplate;
import pl.north93.zgame.datashare.api.cfg.DataSharingGroupConfig;

@SkipInjections
public final class DataSharingGroup
{
    private String       name;
    private Boolean      shareChat;
    @MsgPackCustomTemplate(ArrayListTemplate.class)
    private List<String> dataUnits;

    public DataSharingGroup()
    {
    }

    public DataSharingGroup(final String name, final Boolean shareChat, final List<String> dataUnits)
    {
        this.name = name;
        this.shareChat = shareChat;
        this.dataUnits = dataUnits;
    }

    public DataSharingGroup(final DataSharingGroupConfig config)
    {
        this(config.getName(), config.isShareChat(), config.getDataUnits());
    }

    public String getName()
    {
        return this.name;
    }

    public Boolean getShareChat()
    {
        return this.shareChat;
    }

    public List<String> getDataUnits()
    {
        return this.dataUnits;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("shareChat", this.shareChat).append("dataUnits", this.dataUnits).toString();
    }
}
