package pl.north93.zgame.api.chat.global.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.serializer.platform.annotations.NorthField;

/**
 * Obiekt przechowujący informacje o pokoju czatu.
 * Jest on zapisywany w redisie.
 */
public class ChatRoomData
{
    private String        id;
    private String        parent;
    private Set<String>   children;
    private Integer       priority;
    private String        formatterId;
    @NorthField(type = HashSet.class)
    private Set<Identity> participants;

    // domyślny konstruktor dla serializacji
    public ChatRoomData()
    {
    }

    public ChatRoomData(final String id, final int priority, final String formatterId)
    {
        this.id = id;
        this.children = new HashSet<>();
        this.priority = priority;
        this.formatterId = formatterId;
        this.participants = new HashSet<>();
    }

    public String getId()
    {
        return this.id;
    }

    public String getParent()
    {
        return this.parent;
    }

    public void setParent(final String parent)
    {
        this.parent = parent;
    }

    public Set<String> getChildren()
    {
        return this.children;
    }

    public Integer getPriority()
    {
        return this.priority;
    }

    public String getFormatterId()
    {
        return this.formatterId;
    }

    public void setFormatterId(final String formatterId)
    {
        this.formatterId = formatterId;
    }

    public Set<Identity> getParticipants()
    {
        return this.participants;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("formatterId", this.formatterId).append("participants", this.participants).toString();
    }
}
