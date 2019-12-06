package pl.arieals.minigame.goldhunter.classes;

import java.util.List;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import pl.arieals.minigame.goldhunter.classes.xml.XmlClassEquipmentInfo;
import pl.arieals.minigame.goldhunter.classes.xml.XmlInventoryRefilRule;
import pl.arieals.minigame.goldhunter.classes.xml.XmlPotionEffect;
import pl.arieals.minigame.goldhunter.classes.xml.XmlRewardsInfo;
import pl.arieals.minigame.goldhunter.classes.xml.XmlSpecialAbilityInfo;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.player.PlayerRank;
import pl.north93.northplatform.api.bukkit.utils.xml.itemstack.XmlItemStack;
import pl.north93.northplatform.api.global.messages.TranslatableString;

@RequiredArgsConstructor
public class CharacterClass
{
    @Getter
    private final @Nullable PlayerRank rank;
    @Getter
    private final @Nullable String shopItem;
    @Getter
    private final XmlItemStack icon;
    @Getter
    private final TranslatableString displayName;
    @Getter
    private final TranslatableString lore;
    @Getter
    private final XmlSpecialAbilityInfo specialAbilityInfo;
    @Getter
    private final XmlClassEquipmentInfo equipmentInfo;
    @Getter
    private final List<XmlInventoryRefilRule> inventoryRefilRules;
    @Getter
    private final List<XmlPotionEffect> effects;
    
    private final boolean canBeHealedByPotion;
    @Getter
    private final XmlRewardsInfo rewardsInfo;
    
    public boolean canBeHealedByPotion()
    {
        return canBeHealedByPotion;
    }

    public SpecialAbilityType getSpecialAbility()
    {
        return specialAbilityInfo.getAbilityType();
    }
    
    public int getAbilityLoadingTicks(GoldHunterPlayer player)
    {
        return specialAbilityInfo.getLoadingTime(player) * 20;
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
    
    public boolean hasEnoughRank(GoldHunterPlayer player)
    {
        return rank == null || rank.has(player);
    }
    
    public boolean hasBought(GoldHunterPlayer player)
    {
        return shopItem == null || player.hasBuyed(shopItem, 1);
    }
}
