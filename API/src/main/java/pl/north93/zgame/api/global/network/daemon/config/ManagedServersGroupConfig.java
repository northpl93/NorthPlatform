package pl.north93.zgame.api.global.network.daemon.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.daemon.config.rules.RulesConfig;
import pl.north93.zgame.api.global.network.server.group.ServersGroupType;

@XmlRootElement(name = "managed")
@XmlAccessorType(XmlAccessType.FIELD)
public class ManagedServersGroupConfig extends ServersGroupConfig
{
    @XmlElement
    private String      pattern;
    @XmlElement
    private RulesConfig rules;

    public ManagedServersGroupConfig()
    {
    }

    public String getPattern()
    {
        return this.pattern;
    }

    public RulesConfig getRules()
    {
        return this.rules;
    }

    @Override
    public ServersGroupType getType()
    {
        return ServersGroupType.MANAGED;
    }

    @Override
    public void mergeConfigIntoThis(final ServersGroupConfig config)
    {
        super.mergeConfigIntoThis(config);

        final ManagedServersGroupConfig managedConfig = (ManagedServersGroupConfig) config;
        this.pattern = managedConfig.pattern;
        this.rules = managedConfig.rules;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("pattern", this.pattern).append("rules", this.rules).toString();
    }
}
