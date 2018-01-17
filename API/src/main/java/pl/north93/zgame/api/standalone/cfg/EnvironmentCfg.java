package pl.north93.zgame.api.standalone.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "environment")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentCfg
{
    @XmlElement
    private String id;

    public EnvironmentCfg()
    {
    }

    public EnvironmentCfg(final String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).toString();
    }
}
