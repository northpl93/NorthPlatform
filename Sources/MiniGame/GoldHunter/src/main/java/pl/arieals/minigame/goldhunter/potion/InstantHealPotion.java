package pl.arieals.minigame.goldhunter.potion;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.player.PotionHandler;
import pl.north93.zgame.api.bukkit.utils.nms.NbtTagType;

public class InstantHealPotion implements PotionHandler
{
    @Override
    public boolean applyPotionEffect(GoldHunterPlayer shooter, GoldHunterPlayer target, NBTTagCompound potionData)
    {
        Preconditions.checkArgument(potionData.hasKeyOfType("amount", NbtTagType.TAG_STRING));
        
        if ( !target.getCurrentClass().canBeHealedByPotion() )
        {
            return false;
        }
        if ( shooter.getTeam() != target.getTeam() )
        {
            return false;
        }
        
        double amount = Double.parseDouble(potionData.getString("amount"));
        target.heal(amount);
        return true;
    }
    
}
