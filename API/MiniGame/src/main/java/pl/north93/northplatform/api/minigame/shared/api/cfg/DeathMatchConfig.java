package pl.north93.northplatform.api.minigame.shared.api.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "deathMatch")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeathMatchConfig
{
    @XmlElement
    private Boolean enabled;
    @XmlElement
    private String  templateName;

    public Boolean getEnabled()
    {
        return this.enabled;
    }

    public String getTemplateName()
    {
        return this.templateName;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("enabled", this.enabled).append("templateName", this.templateName).toString();
    }
}
