package pl.arieals.minigame.goldhunter.classes;

import java.util.List;

import javax.annotation.Nullable;

import pl.arieals.minigame.goldhunter.classes.xml.XmlClassEquipmentInfo;
import pl.arieals.minigame.goldhunter.classes.xml.XmlInventoryRefilRule;
import pl.arieals.minigame.goldhunter.classes.xml.XmlPotionEffect;
import pl.arieals.minigame.goldhunter.classes.xml.XmlSpecialAbilityInfo;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.player.PlayerRank;
import pl.north93.zgame.api.bukkit.utils.xml.itemstack.XmlItemStack;
import pl.north93.zgame.api.global.messages.TranslatableString;

public class CharacterClass
{
    private final @Nullable PlayerRank rank;
    private final @Nullable String shopItem;
    private final XmlItemStack icon;
    
    private final TranslatableString displayName;
    private final TranslatableString lore;
    
    private final XmlSpecialAbilityInfo specialAbilityInfo;
    
    private final XmlClassEquipmentInfo equipmentInfo;

    private final List<XmlInventoryRefilRule> inventoryRefilRules;
    
    private final List<XmlPotionEffect> effects;
    
    private final boolean canBeHealedByPotion;
    
    // XXX: rewrite this
    public CharacterClass(PlayerRank rank, String shopItem, XmlItemStack icon, TranslatableString displayName, TranslatableString lore, 
            XmlSpecialAbilityInfo specialAbility, XmlClassEquipmentInfo equipmentInfo, List<XmlInventoryRefilRule> inventoryRefilRules, 
            List<XmlPotionEffect> effects, boolean canBeHealedByPotion)
    {
        this.rank = rank;
        this.shopItem = shopItem;
        this.icon = icon;
        this.displayName = displayName;
        this.lore = lore;
        this.specialAbilityInfo = specialAbility;
        this.equipmentInfo = equipmentInfo;
        this.inventoryRefilRules = inventoryRefilRules;
        this.effects = effects;
        this.canBeHealedByPotion = canBeHealedByPotion;
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
    
    public TranslatableString getLore()
    {
        return lore;
    }
    
    public boolean canBeHealedByPotion()
    {
        return canBeHealedByPotion;
    }
    
    public XmlSpecialAbilityInfo getSpecialAbilityInfo()
    {
        return specialAbilityInfo;
    }

    public SpecialAbilityType getSpecialAbility()
    {
        return specialAbilityInfo.getAbilityType();
    }
    
    public int getAbilityLoadingTicks(GoldHunterPlayer player)
    {
        return specialAbilityInfo.getLoadingTime(player) * 20;
    }
    
    public XmlClassEquipmentInfo getEquipmentInfo()
    {
        return equipmentInfo;
    }
    
    public void applyEffects(GoldHunterPlayer player)
    {
        player.getPlayer().getActivePotionEffects().forEach(effect -> player.getPlayer().removePotionEffect(effect.getType()));
        effects.forEach(effect -> effect.applyToPlayer(player));
    }
    
    public void applyEquipment(GoldHunterPlayer player)
    {
        equipmentInfo.applyToPlayer(player);
    }
    
    public List<XmlInventoryRefilRule> getInventoryRefilRules()
    {
        return inventoryRefilRules;
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
