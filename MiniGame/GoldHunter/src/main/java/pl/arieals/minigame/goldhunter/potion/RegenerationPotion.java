package pl.arieals.minigame.goldhunter.potion;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

import pl.arieals.minigame.goldhunter.effect.RegenerationEffect;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.player.PotionHandler;
import pl.north93.zgame.api.bukkit.utils.nms.NbtTagType;

public class RegenerationPotion implements PotionHandler
{
    @Override
    public boolean applyPotionEffect(GoldHunterPlayer shooter, GoldHunterPlayer target, NBTTagCompound potionData)
    {
        Preconditions.checkArgument(potionData.hasKeyOfType("duration", NbtTagType.TAG_STRING));
        Preconditions.checkArgument(potionData.hasKeyOfType("value", NbtTagType.TAG_STRING));
        
        if ( !target.getCurrentClass().canBeHealedByPotion() )
        {
            return false;
        }
        
        if ( target.getTeam() != shooter.getTeam() )
        {
            return false;
        }
        
        int duration = Integer.parseInt(potionData.getString("duration"));
        double value = Double.parseDouble(potionData.getString("value"));
                
        target.getEffectTracker().addEffect(new RegenerationEffect(value), duration);
        return true;
    }   
}
