package pl.north93.zgame.datashare.api.cfg;

import static org.diorite.cfg.annotations.CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE;


import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgCollectionStyle;
import org.diorite.cfg.annotations.CfgComment;

import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.ArrayListTemplate;

public class AnnouncerConfig
{
    @CfgComment("Czy wlaczony")
    private Boolean      enabled;
    @CfgComment("Czas w sekundach pomiedzy wiadomosciami")
    private Integer      time;
    @CfgCollectionStyle(ALWAYS_NEW_LINE)
    @CfgComment("Lista wiadomosci")
    @MsgPackCustomTemplate(ArrayListTemplate.class)
    private List<String> messages;

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
