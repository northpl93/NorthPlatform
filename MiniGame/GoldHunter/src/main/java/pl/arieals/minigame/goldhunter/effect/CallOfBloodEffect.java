package pl.arieals.minigame.goldhunter.effect;

import java.util.Arrays;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.arieals.minigame.goldhunter.Effect;
import pl.north93.zgame.api.bukkit.utils.itemstack.MaterialUtils;

public class CallOfBloodEffect extends Effect
{
    public CallOfBloodEffect()
    {
        setBarColor(EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        addEnchantsToSwords();
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().getPlayer().removePotionEffect(PotionEffectType.SPEED);
        removeEnchantsFromSwords();
    }
    
    private void addEnchantsToSwords()
    {
        PlayerInventory inv = getPlayer().getPlayer().getInventory();
        
        Arrays.stream(inv.getContents()).filter(is -> is != null && MaterialUtils.isSword(is.getType())).forEach(this::applyEnchant);
    }
    
    private void applyEnchant(ItemStack is)
    {
        ItemMeta meta = is.getItemMeta();
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        is.setItemMeta(meta);
    }
    
    private void removeEnchantsFromSwords()
    {
        PlayerInventory inv = getPlayer().getPlayer().getInventory();
        
        Arrays.stream(inv.getContents()).filter(is -> is != null && MaterialUtils.isSword(is.getType())).forEach(this::removeEnchant);
    }
    
    private void removeEnchant(ItemStack is)
    {
        ItemMeta meta = is.getItemMeta();
        meta.removeEnchant(Enchantment.DAMAGE_ALL);
        is.setItemMeta(meta);
    }
}
