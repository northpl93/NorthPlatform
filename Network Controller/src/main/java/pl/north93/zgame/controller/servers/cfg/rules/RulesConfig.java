package pl.north93.zgame.controller.servers.cfg.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.LinkedListTemplate;

@XmlRootElement(name = "rules")
@XmlAccessorType(XmlAccessType.FIELD)
public class RulesConfig
{
    @XmlAttribute(name = "min")
    private Integer               minServers = 0;
    @XmlAttribute(name = "max")
    private Integer               maxServers = Integer.MAX_VALUE;
    @XmlElement(name = "value")
    @MsgPackCustomTemplate(LinkedListTemplate.class)
    private List<RuleEntryConfig> rules = new LinkedList<>();

    public int getMinServers()
    {
        return this.minServers;
    }

    public int getMaxServers()
    {
        return this.maxServers;
    }

    public List<RuleEntryConfig> getRules()
    {
        return this.rules;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("minServers", this.minServers).append("maxServers", this.maxServers).append("rules", this.rules).toString();
    }
}
