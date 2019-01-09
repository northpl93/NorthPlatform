package pl.north93.northplatform.api.bukkit.hologui.hologram.loader;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "holograms")
@XmlAccessorType(XmlAccessType.NONE)
public class HologramsConfig
{
    @XmlElement(name = "hologram")
    private List<HologramEntryConfig> holograms;

    public List<HologramEntryConfig> getHolograms()
    {
        return this.holograms;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("holograms", this.holograms).toString();
    }
}
