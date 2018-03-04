package pl.arieals.minigame.goldhunter.classes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.itemstack.ItemStackBuilder;

public enum InventoryRefilRule
{
    LUCZNIK
    {
        @Override
        public void tryRefilEquipment(GoldHunterPlayer player)
        {
            player.tryRefilItem(new ItemStackBuilder().material(Material.ARROW).build(), 3);
        }
    },
    MEDYK_POLOWY
    {
        @Override
        public void tryRefilEquipment(GoldHunterPlayer player)
        {
            ItemStack is = new ItemStackBuilder().material(Material.SPLASH_POTION).build();
            PotionMeta meta = (PotionMeta) is.getItemMeta();
            meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
            is.setItemMeta(meta);
            
            player.tryRefilItem(is, 5);
        }
    }
    ;
    
    public void tryRefilEquipment(GoldHunterPlayer player)
    {
        
    }
    
    public static InventoryRefilRule byName(String name)
    {
        if ( name == null )
        {
            return null;
        }
        try
        {
            return valueOf(name);
        }
        catch ( EnumConstantNotPresentException e )
        {
            return null;
        }
    }
}
