package pl.north93.zgame.restful.models;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class PlayerModel
{
    private UUID    uuid;
    private String  nick;
    private boolean isOnline;
    private String  group;

    public PlayerModel(final UUID uuid, final String nick, final boolean isOnline, final String group)
    {
        this.uuid = uuid;
        this.nick = nick;
        this.isOnline = isOnline;
        this.group = group;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("nick", this.nick).append("isOnline", this.isOnline).append("group", this.group).toString();
    }
}
