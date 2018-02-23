package pl.arieals.minigame.goldhunter.classes;

import java.util.Optional;

import javax.annotation.Nullable;

import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.InventoryRefilRule;
import pl.arieals.minigame.goldhunter.PlayerRank;
import pl.arieals.minigame.goldhunter.SpecialAbilityType;
import pl.arieals.minigame.goldhunter.classes.xml.XmlClassEquipmentInfo;
import pl.arieals.minigame.goldhunter.entity.GoldHunterEntityUtils;
import pl.north93.zgame.api.bukkit.utils.xml.itemstack.XmlItemStack;
import pl.north93.zgame.api.global.messages.TranslatableString;

public class CharacterClass
{
    private final @Nullable PlayerRank rank;
    private final @Nullable String shopItem;
    private final XmlItemStack icon;
    
    private final TranslatableString displayName;
    private final TranslatableString shortName;
    private final TranslatableString lore;
    
    private final @Nullable SpecialAbilityType specialAbility;
    
    private final XmlClassEquipmentInfo equipmentInfo;

    private final @Nullable InventoryRefilRule inventoryRefilRule;
    private final int inventoryRefilTime;
    
    public CharacterClass(PlayerRank rank, String shopItem, XmlItemStack icon, TranslatableString displayName, TranslatableString shortName, TranslatableString lore, 
            SpecialAbilityType specialAbility, XmlClassEquipmentInfo equipmentInfo, InventoryRefilRule inventoryRefilRule, int inventoryRefilTime)
    {
        this.rank = rank;
        this.shopItem = shopItem;
        this.icon = icon;
        this.displayName = displayName;
        this.shortName = shortName;
        this.lore = lore;
        this.specialAbility = specialAbility;
        this.equipmentInfo = equipmentInfo;
        this.inventoryRefilRule = inventoryRefilRule;
        this.inventoryRefilTime = inventoryRefilTime;
    }
    
    public PlayerRank getRank()
    {
        return rank;
    }
    
    public String getShopItem()
    {
        return shopItem;
    }
    
    public XmlItemStack getIcon()
    {
        return icon;
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
    
    public InventoryRefilRule getInventoryRefilRule()
    {
        return inventoryRefilRule;
    }
    
    public int getInventoryRefilTime()
    {
        return inventoryRefilTime;
    }
    
    public boolean hasEnoughRank(GoldHunterPlayer player)
    {
        return rank == null || rank.has(player);
    }
    
    public boolean hasBought(GoldHunterPlayer player)
    {
        return shopItem == null || player.hasBuyed(shopItem, 1);
    }
}
