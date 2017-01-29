package pl.north93.zgame.timegroup.shared;

import java.util.UUID;

public class GroupExpireInfo
{
    private UUID   playerId;
    private String group;
    private long   givenAt;
    private long   expireAt;

    public GroupExpireInfo(final UUID playerId, final String group, final long givenAt, final long expireAt)
    {
        this.playerId = playerId;
        this.group = group;
        this.givenAt = givenAt;
        this.expireAt = expireAt;
    }

    public UUID getPlayerId()
    {
        return this.playerId;
    }

    public void setPlayerId(final UUID playerId)
    {
        this.playerId = playerId;
    }

    public String getGroup()
    {
        return this.group;
    }

    public void setGroup(final String group)
    {
        this.group = group;
    }

    public long getGivenAt()
    {
        return this.givenAt;
    }

    public void setGivenAt(final long givenAt)
    {
        this.givenAt = givenAt;
    }

    public long getExpireAt()
    {
        return this.expireAt;
    }

    public void setExpireAt(final long expireAt)
    {
        this.expireAt = expireAt;
    }
}
