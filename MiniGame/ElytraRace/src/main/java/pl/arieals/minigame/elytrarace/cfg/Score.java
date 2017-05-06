package pl.arieals.minigame.elytrarace.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.zgame.api.bukkit.utils.xml.XmlCuboid;

@XmlRootElement(name = "score_point")
@XmlAccessorType(XmlAccessType.FIELD)
public class Score
{
    @XmlElement(required = true)
    private XmlCuboid area;
}
