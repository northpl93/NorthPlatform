package pl.north93.zgame.api.chat.global.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.HashSetTemplate;

/**
 * Obiekt przechowujący informacje o pokoju czatu.
 * Jest on zapisywany w redisie.
 */
public class ChatRoomData
{
    private String        formatterId;
    @MsgPackCustomTemplate(HashSetTemplate.class)
    private Set<Identity> participants;

    // domyślny konstruktor dla serializacji
    public ChatRoomData()
    {
    }

    public ChatRoomData(final String formatterId)
    {
        this.formatterId = formatterId;
        this.participants = new HashSet<>();
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
