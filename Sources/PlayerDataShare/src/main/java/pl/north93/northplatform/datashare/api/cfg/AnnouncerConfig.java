package pl.north93.northplatform.datashare.api.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.serializer.platform.annotations.NorthField;

@XmlAccessorType(XmlAccessType.FIELD)
public class AnnouncerConfig
{
    @XmlElement
    private Boolean      enabled; // Czy wlaczony
    @XmlElement
    private Integer      time; // Czas w sekundach pomiedzy wiadomosciami
    @XmlElementWrapper(name = "messages")
    @XmlElement(name = "message")
    @NorthField(type = ArrayList.class)
    private List<String> messages; // Lista wiadomosci

    public AnnouncerConfig()
    {
    }

    public Boolean isEnabled()
    {
        return this.enabled;
    }

    public Integer getTime()
    {
        return this.time;
    }

    public List<String> getMessages()
    {
        return this.messages;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("enabled", this.enabled).append("time", this.time).append("messages", this.messages).toString();
    }
}
