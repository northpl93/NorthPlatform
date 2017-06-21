package pl.arieals.minigame.elytrarace.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.ElytraRaceMode;

@XmlRootElement(name = "elytra")
@XmlAccessorType(XmlAccessType.FIELD)
public class ElytraConfig
{
    @XmlElement(required = true)
    private ElytraRaceMode mode;

    public ElytraRaceMode getMode()
    {
        return this.mode;
    }

    public void setMode(final ElytraRaceMode mode)
    {
        this.mode = mode;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mode", this.mode).toString();
    }
}
