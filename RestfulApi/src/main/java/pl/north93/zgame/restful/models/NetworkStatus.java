package pl.north93.zgame.restful.models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.JoiningPolicy;

public class NetworkStatus
{
    private int           slots;
    private int           online;
    private JoiningPolicy joiningPolicy;
    private String        messageOfTheDay;

    public NetworkStatus(final int slots, final int online, final JoiningPolicy joiningPolicy, final String messageOfTheDay)
    {
        this.slots = slots;
        this.online = online;
        this.joiningPolicy = joiningPolicy;
        this.messageOfTheDay = messageOfTheDay;
    }

    public int getSlots()
    {
        return this.slots;
    }

    public int getOnline()
    {
        return this.online;
    }

    public JoiningPolicy getJoiningPolicy()
    {
        return this.joiningPolicy;
    }

    public String getMessageOfTheDay()
    {
        return this.messageOfTheDay;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("slots", this.slots).append("online", this.online).append("joiningPolicy", this.joiningPolicy).append("messageOfTheDay", this.messageOfTheDay).toString();
    }
}
