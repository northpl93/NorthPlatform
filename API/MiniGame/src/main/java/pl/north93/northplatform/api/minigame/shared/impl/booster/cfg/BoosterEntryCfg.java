package pl.north93.northplatform.api.minigame.shared.impl.booster.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlAccessorType(XmlAccessType.NONE)
public class BoosterEntryCfg
{
    @XmlAttribute
    private String  id;
    @XmlAttribute
    private Boolean enabled;

    public String getId()
    {
        return this.id;
    }

    public Boolean getEnabled()
    {
        return this.enabled;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).append("enabled", this.enabled).toString();
    }
}
