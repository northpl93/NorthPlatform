package pl.arieals.minigame.goldhunter.potion;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

import pl.arieals.minigame.goldhunter.effect.SlownessEffect;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.player.PotionHandler;
import pl.north93.zgame.api.bukkit.utils.nms.NbtTagType;

public class SlownessPotion implements PotionHandler
{
    @Override
    public boolean applyPotionEffect(GoldHunterPlayer shooter, GoldHunterPlayer target, NBTTagCompound potionData)
    {
        Preconditions.checkArgument(potionData.hasKeyOfType("duration", NbtTagType.TAG_STRING));
        
        if ( shooter.getTeam() == target.getTeam() )
        {
            return false;
        }
        
        int duration = Integer.parseInt(potionData.getString("duration"));
        target.getEffectTracker().addEffect(new SlownessEffect(), duration);
        return true;
    }
}
