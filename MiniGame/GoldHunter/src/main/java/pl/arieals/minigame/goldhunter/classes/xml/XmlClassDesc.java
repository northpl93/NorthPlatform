package pl.arieals.minigame.goldhunter.classes.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import pl.arieals.minigame.goldhunter.classes.CharacterClass;
import pl.arieals.minigame.goldhunter.classes.InventoryRefilRule;
import pl.arieals.minigame.goldhunter.classes.SpecialAbilityType;
import pl.arieals.minigame.goldhunter.player.PlayerRank;
import pl.north93.zgame.api.bukkit.utils.xml.itemstack.XmlItemStack;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;

@XmlRootElement(name = "class")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlClassDesc
{
    private PlayerRank rank;
    private String shopItem;
    private XmlItemStack icon;
    
    private String displayName;
    private String shortName;
    private String lore;
    
    private String specialAbility;
    
    private String invRefilRule;
    private int invRefilTime;
    
    private XmlClassEquipmentInfo equipment;
    
    public String getDisplayName()
    {
        return displayName;
    }
    
    public String getShortName()
    {
        return shortName;
    }
    
    public String getLore()
    {
        return lore;
    }
    
    public String getSpecialAbility()
    {
        return specialAbility;
    }
    
    public XmlClassEquipmentInfo getEquipment()
    {
        return equipment;
    }
    
    public CharacterClass toCharacterClass(MessagesBox messagesBox)
    {
        return new CharacterClass(rank,
                shopItem,
                icon != null ? icon : new XmlItemStack("BEDROCK"),
                TranslatableString.of(messagesBox, displayName),
                TranslatableString.of(messagesBox, shortName),
                TranslatableString.of(messagesBox, lore),
                SpecialAbilityType.byName(specialAbility),
                equipment,
                InventoryRefilRule.byName(invRefilRule),
                invRefilTime);
    }
}
