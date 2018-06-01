package pl.arieals.api.minigame.shared.impl.booster.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "boosters")
@XmlAccessorType(XmlAccessType.NONE)
public class BoostersCfg
{
    @XmlElement(name = "booster")
    private List<BoosterEntryCfg> boosters;

    public List<BoosterEntryCfg> getBoosters()
    {
        return this.boosters;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("boosters", this.boosters).toString();
    }
}
