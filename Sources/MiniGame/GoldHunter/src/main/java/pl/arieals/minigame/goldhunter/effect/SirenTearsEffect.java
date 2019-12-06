package pl.arieals.minigame.goldhunter.effect;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.utils.itemstack.ItemStackBuilder;

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
        getPlayer().getAbilityTracker().suspendAbilityLoading();
        setGreenBanner();
    }
    
    @SuppressWarnings("deprecation")
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
        getPlayer().getAbilityTracker().resetAbilityLoading();
        getPlayer().addLeatherHatToInventory();
        getPlayer().setLeatherArmorColor();
    }
    
    @Override
    protected void handlePlayer(GoldHunterPlayer player)
    {
        if ( getPlayer().getTeam() == player.getTeam() )
        {
            player.getEffectTracker().addEffect(new RegenerationEffect(2), 60);
        }
    }
}
