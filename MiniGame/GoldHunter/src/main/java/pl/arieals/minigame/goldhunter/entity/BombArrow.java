package pl.arieals.minigame.goldhunter.entity;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import net.minecraft.server.v1_10_R1.EntityLiving;

public class BombArrow extends HomingArrow
{
    public BombArrow(World world, LivingEntity shooter, LivingEntity target)
    {
        super(world, shooter, target);
    }
    
    @Override
    protected void onTick()
    {
        super.onTick();
        
        if ( onGround )
        {
            die();
            makeExplosion();
        }
    }
    
    @Override
    protected void a(EntityLiving entityliving)
    {
        makeExplosion();
    }
    
    private void makeExplosion()
    {
        world.getWorld().createExplosion(locX, locY - 1, locZ, 3, false, false);
    }
}
