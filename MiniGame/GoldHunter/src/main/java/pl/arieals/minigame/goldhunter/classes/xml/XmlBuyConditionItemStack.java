package pl.arieals.minigame.goldhunter.classes.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.xml.itemstack.XmlItemStack;


@XmlAccessorType(XmlAccessType.NONE)
public class XmlBuyConditionItemStack extends XmlItemStack
{
    @XmlAttribute(name = "ifbuyed")
    private String condition;
    
    public String getCondition()
    {
        return condition;
    }
    
    public boolean check(GoldHunterPlayer player)
    {
        if ( condition == null )
        {
            return true;
        }
        
        String[] split = condition.split(":");
        String shopItemName = split[0];
        
        int shopItemLevel = 1;
        if ( split.length > 1 ) 
        {
            Integer.parseInt(split[1]);
        }
        
        return player.hasBuyed(shopItemName, shopItemLevel);
    }
}
