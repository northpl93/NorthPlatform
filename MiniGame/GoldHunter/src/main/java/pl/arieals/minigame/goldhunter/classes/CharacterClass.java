package pl.arieals.minigame.goldhunter.classes;

import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.SpecialAbilityType;
import pl.arieals.minigame.goldhunter.classes.xml.XmlClassEquipmentInfo;
import pl.north93.zgame.api.global.messages.TranslatableString;

public class CharacterClass
{
    private final TranslatableString displayName;
    private final TranslatableString shortName;
    private final TranslatableString lore;
    
    private final SpecialAbilityType specialAbility;
    
    private final XmlClassEquipmentInfo equipmentInfo;

    public CharacterClass(TranslatableString displayName, TranslatableString shortName, TranslatableString lore, SpecialAbilityType specialAbility,
            XmlClassEquipmentInfo equipmentInfo)
    {
        this.displayName = displayName;
        this.shortName = shortName;
        this.lore = lore;
        this.specialAbility = specialAbility;
        this.equipmentInfo = equipmentInfo;
    }
    
    public TranslatableString getDisplayName()
    {
        return displayName;
    }
    
    public TranslatableString getShortName()
    {
        return shortName;
    }
    
    public TranslatableString getLore()
    {
        return lore;
    }
    
    public SpecialAbilityType getSpecialAbility()
    {
        return specialAbility;
    }
    
    public XmlClassEquipmentInfo getEquipmentInfo()
    {
        return equipmentInfo;
    }
    
    public void applyEquipment(GoldHunterPlayer player)
    {
        equipmentInfo.applyToPlayer(player);
    }
}
