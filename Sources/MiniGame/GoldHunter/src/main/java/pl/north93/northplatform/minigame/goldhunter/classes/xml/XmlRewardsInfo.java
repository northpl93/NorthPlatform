package pl.north93.northplatform.minigame.goldhunter.classes.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.NONE)
public class XmlRewardsInfo
{
    @XmlElement(name = "kill")
    private double killReward;
    @XmlElement(name = "assist")
    private double assistReward;
    @XmlElement(name = "specialAbility")
    private double specialAbilityReward;
    @XmlElement(name = "chestDestroy")
    private double chestDestroyReward;
}
