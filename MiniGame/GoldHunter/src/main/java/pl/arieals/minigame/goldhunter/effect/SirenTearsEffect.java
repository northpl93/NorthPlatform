package pl.arieals.minigame.goldhunter.effect;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.itemstack.ItemStackBuilder;

public class SirenTearsEffect extends RadiusEffect
{
    public SirenTearsEffect(double radius)
    {
        super(radius);

        setBarColor(EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        setGreenBanner();
    }
    
    private void setGreenBanner()
    {
        ItemStack banner = new ItemStackBuilder().material(Material.BANNER).build();
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.setBaseColor(DyeColor.GREEN);
        banner.setItemMeta(meta);
        
        getPlayer().getPlayer().getInventory().setHelmet(banner);
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().addLeatherHatToInventory();
        getPlayer().setLeatherArmorColor();
    }
    
    @Override
    protected void handlePlayer(GoldHunterPlayer player)
    {
        player.getEffectTracker().addEffect(new HealingEffect(), 100);
    }
}
