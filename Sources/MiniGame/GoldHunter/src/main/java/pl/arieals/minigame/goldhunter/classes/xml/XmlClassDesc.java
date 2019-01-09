package pl.arieals.minigame.goldhunter.classes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;

import pl.arieals.minigame.goldhunter.classes.CharacterClass;
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
    private String lore;
    
    private XmlSpecialAbilityInfo specialAbility;
    
    private XmlClassEquipmentInfo equipment;
    
    private boolean canBeHealedByPotion = true;
    
    @XmlElement(name = "invRefilRule")
    private List<XmlInventoryRefilRule> invRefilRules = new ArrayList<>();
    
    @XmlElementWrapper(name = "effects")
    @XmlElement(name = "effect")
    private List<XmlPotionEffect> effects = new ArrayList<>();
    
    private XmlRewardsInfo rewards;
    
    public String getDisplayName()
    {
        return displayName;
    }
    
    public String getLore()
    {
        return lore;
    }
    
    public XmlSpecialAbilityInfo getSpecialAbility()
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
                TranslatableString.of(messagesBox, lore),
                specialAbility,
                equipment,
                ImmutableList.copyOf(invRefilRules),
                effects,
                canBeHealedByPotion,
                rewards);
    }
}
