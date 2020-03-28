package pl.north93.northplatform.minigame.goldhunter.effect;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.utils.itemstack.ItemStackBuilder;

public class BattlecryEffect extends RadiusEffect
{
    public BattlecryEffect(double radius)
    {
        super(radius);
        
        setBarColor(EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        getPlayer().getAbilityTracker().suspendAbilityLoading();
        setRedBanner();
    }
    
    @SuppressWarnings("deprecation")
    private void setRedBanner()
    {
        ItemStack banner = new ItemStackBuilder().material(Material.BANNER).build();
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.setBaseColor(DyeColor.RED);
        banner.setItemMeta(meta);
        
        getPlayer().getPlayer().getInventory().setHelmet(banner);
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().addLeatherHatToInventory();
        getPlayer().setLeatherArmorColor();
        getPlayer().getAbilityTracker().resetAbilityLoading();
    }
    
    @Override
    protected void handlePlayer(GoldHunterPlayer player)
    {
        if ( getPlayer().getTeam() == player.getTeam() )
        {
            player.getEffectTracker().addEffect(new StrengthEffect(), 60);
        }
    }
}
