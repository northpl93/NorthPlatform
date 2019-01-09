package pl.arieals.minigame.goldhunter.classes.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class XmlAbilityLoadingTime implements BuyConditionElement
{
    @XmlAttribute(name = "ifbuyed")
    private String condition;
    
    @XmlAttribute(name = "seconds")
    private int time;
    
    public int getTime()
    {
        return time;
    }
    
    @Override
    public String getCondition()
    {
        return condition;
    }
}
