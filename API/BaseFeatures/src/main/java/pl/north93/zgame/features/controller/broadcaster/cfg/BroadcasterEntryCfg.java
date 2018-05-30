package pl.north93.zgame.features.controller.broadcaster.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlAccessorType(XmlAccessType.NONE)
public class BroadcasterEntryCfg
{
    @XmlElementWrapper(name = "rooms")
    @XmlElement(name = "room")
    private List<String>                rooms;
    @XmlElementWrapper(name = "messages")
    @XmlElement(name = "message")
    private List<BroadcasterMessageCfg> messages;
    @XmlElement
    private Integer                     interval;

    public List<String> getRooms()
    {
        return this.rooms;
    }

    public List<BroadcasterMessageCfg> getMessages()
    {
        return this.messages;
    }

    public Integer getInterval()
    {
        return this.interval;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("rooms", this.rooms).append("messages", this.messages).append("interval", this.interval).toString();
    }
}
