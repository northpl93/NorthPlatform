package pl.arieals.minigame.goldhunter.player;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public interface PotionHandler
{
    boolean applyPotionEffect(GoldHunterPlayer shooter, GoldHunterPlayer target, NBTTagCompound potionData);
}
