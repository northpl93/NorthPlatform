package pl.arieals.minigame.goldhunter.classes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import pl.arieals.minigame.goldhunter.classes.SpecialAbilityType;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlSpecialAbilityInfo
{
    @XmlAttribute(name = "type")
    private SpecialAbilityType abilityType;
    
    @XmlElement(name = "loading")
    private List<XmlAbilityLoadingTime> loadingTimes = new ArrayList<>();
    
    public SpecialAbilityType getAbilityType()
    {
        return abilityType;
    }
    
    public int getLoadingTime(GoldHunterPlayer player)
    {
        return loadingTimes.stream().filter(time -> time.check(player)).mapToInt(XmlAbilityLoadingTime::getTime).findFirst().orElse(0);
    }
}
