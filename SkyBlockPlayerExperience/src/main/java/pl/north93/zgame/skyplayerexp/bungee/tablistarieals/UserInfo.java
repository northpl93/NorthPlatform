package pl.north93.zgame.skyplayerexp.bungee.tablistarieals;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserInfo
{
    private UUID    uuid;
    private Boolean remove;
    private Boolean isYoutuber;
    private String  name;

    public UserInfo()
    {
    }

    public UserInfo(final UUID uuid, final Boolean remove, final Boolean isYoutuber, final String name)
    {
        this.uuid = uuid;
        this.remove = remove;
        this.isYoutuber = isYoutuber;
        this.name = name;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public Boolean getRemove()
    {
        return this.remove;
    }

    public Boolean getYoutuber()
    {
        return this.isYoutuber;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("remove", this.remove).append("isYoutuber", this.isYoutuber).append("name", this.name).toString();
    }
}
