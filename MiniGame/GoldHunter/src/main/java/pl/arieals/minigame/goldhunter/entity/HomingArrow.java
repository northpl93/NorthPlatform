package pl.arieals.minigame.goldhunter.entity;

import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_10_R1.EntityLiving;
import net.minecraft.server.v1_10_R1.EntityTippedArrow;
import net.minecraft.server.v1_10_R1.ItemStack;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class HomingArrow extends EntityTippedArrow
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    private EntityLiving target;
    
    public HomingArrow(org.bukkit.World world, LivingEntity shooter, LivingEntity target)
    {
        super(((CraftWorld) world).getHandle(), ((CraftLivingEntity) shooter).getHandle());
        
        if ( target != null )
        { 
            this.target = ((CraftLivingEntity) target).getHandle();
        }
        
        this.fromPlayer = PickupStatus.DISALLOWED;
    }
    
    public void addEntityToWorld()
    {
        world.addEntity(this);
    }
    
    public void shoot(float force)
    {
        a(shooter, shooter.pitch, shooter.yaw, 0.0F, force * 3.0F, 1.0F);
    }

    @Override
    protected ItemStack j()
    {
        return null;
    }
    
    @Override
    public boolean c(NBTTagCompound nbttagcompound)
    {
        return false;
    }
    
    @Override
    public boolean d(NBTTagCompound nbttagcompound)
    {
        return false;
    }
    
    @Override
    public final void m() // onTick
    {
        super.m();
        
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
    
    protected void onTick() {
        
        if ( target == null )
        {
            return;
        }
        
        if ( this.inGround || target.getWorld() != this.getWorld() || target.dead )
        {
            target = null;
            return;
        }
        
        handleTarget();
    }
    
    private void handleTarget()
    {
        double currentSpeed = getBukkitEntity().getVelocity().length();
        
        Vector toTarget = target.getBukkitEntity().getLocation().add(0, 1.3, 0).subtract(getBukkitEntity().getLocation()).toVector();
        
        Vector velocityDir = getBukkitEntity().getVelocity().normalize();
        Vector toTargetDir = toTarget.clone().normalize();
        
        double distance = target.getBukkitEntity().getLocation().distance(getBukkitEntity().getLocation());
        double targetRatio = 1 / Math.sqrt(distance);
        
        Vector newDir;
        if ( targetRatio > 1 )
        {
            newDir = toTargetDir;
        }
        else
        {
            newDir = velocityDir.clone().multiply(1 - targetRatio).add(toTargetDir.multiply(targetRatio));
        }
        
        Vector newVelocity = newDir.normalize().multiply(currentSpeed);
        
        getBukkitEntity().setVelocity(newVelocity);
    }
}
