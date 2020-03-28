package pl.north93.northplatform.minigame.goldhunter.potion;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

import pl.north93.northplatform.minigame.goldhunter.effect.PoisonEffect;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.minigame.goldhunter.player.PotionHandler;
import pl.north93.northplatform.api.bukkit.utils.nms.NbtTagType;

public class PoisonPotion implements PotionHandler
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
        target.getEffectTracker().addEffect(new PoisonEffect(), duration);
        return true;
    }
}
