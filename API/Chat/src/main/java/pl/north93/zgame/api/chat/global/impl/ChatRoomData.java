package pl.north93.zgame.api.chat.global.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.ArrayListTemplate;

/*default*/ class ChatRoomData
{
    private String         formatterId;
    @MsgPackCustomTemplate(ArrayListTemplate.class)
    private List<Identity> participants;

    public ChatRoomData(final String formatterId)
    {
        this.formatterId = formatterId;
        this.participants = new ArrayList<>();
    }

    public String getFormatterId()
    {
        return this.formatterId;
    }

    public void setFormatterId(final String formatterId)
    {
        this.formatterId = formatterId;
    }

    public List<Identity> getParticipants()
    {
        return this.participants;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("formatterId", this.formatterId).append("participants", this.participants).toString();
    }
}
