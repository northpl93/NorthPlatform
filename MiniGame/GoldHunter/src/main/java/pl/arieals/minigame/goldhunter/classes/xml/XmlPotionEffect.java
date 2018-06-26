package pl.arieals.minigame.goldhunter.classes.xml;

import javax.xml.bind.annotation.XmlAttribute;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

public class XmlPotionEffect
{
    @XmlAttribute
    private String type;
    
    @XmlAttribute
    private int level = 1;
    
    public String getType()
    {
        return type;
    }
    
    public int getLevel()
    {
        return level;
    }
    
    public void applyToPlayer(GoldHunterPlayer player)
    {
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.getByName(type), Integer.MAX_VALUE, level, true, true), true);
    }
}

