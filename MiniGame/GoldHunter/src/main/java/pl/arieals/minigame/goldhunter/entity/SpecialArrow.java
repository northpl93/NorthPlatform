package pl.arieals.minigame.goldhunter.entity;

import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTippedArrow;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public abstract class SpecialArrow extends EntityTippedArrow
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    @Inject
    private static GoldHunter goldHunter;
    
    public SpecialArrow(org.bukkit.World world, LivingEntity shooter)
    {
        super(((CraftWorld) world).getHandle(), ((CraftLivingEntity) shooter).getHandle());
        
        this.fromPlayer = PickupStatus.DISALLOWED;
    }
    
    public final void shoot(float force)
    {
        a(shooter, shooter.pitch, shooter.yaw, 0.0F, force * 3.0F, 1.0F);
        world.addEntity(this);
    }

    // w 1.10 ta metoda istniala w klasie strzaly z NMS
    public final boolean isInGround()
    {
        return this.inGround;
    }

    @Override
    protected final ItemStack j()
    {
        return null;
    }
    
    @Override
    public final boolean c(NBTTagCompound nbttagcompound)
    {
        return false;
    }
    
    @Override
    public final boolean d(NBTTagCompound nbttagcompound)
    {
        return false;
    }
    
    @Override
    protected final void a(EntityLiving entityLiving)
    {
        if ( !( entityLiving instanceof EntityPlayer ) )
        {
           return;
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer(((EntityPlayer) entityLiving).getBukkitEntity());
        
        logger.debug("Special arrow {} hits player {}", () -> getClass().getSimpleName(), () -> player.getPlayer().getName());
        
        try
        {
            onHitPlayer(player);
        }
        catch ( Throwable e )
        {
            logger.error("onHitPlayer throws an exception:", e);
        }
    }
    
    @Override
    public final void B_() // onTick
    {
        super.B_();
        
        try
        {
            onTick();
        }
        catch ( Throwable e )
        {
            // prevent server crash on exception
            logger.error("An exception was throw when ticking homing arrow", e);
        }
    }
    
    protected void onHitPlayer(GoldHunterPlayer player)
    {
    }
    
    protected abstract void onTick();
}

