package pl.arieals.minigame.goldhunter.entity;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import net.minecraft.server.v1_12_R1.EntityLiving;

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
        
        if ( inGround )
        {
            die();
            makeExplosion();
            return;
        }
        
        spawnParticle();
    }
    
    private void spawnParticle()
    {
        if ( !isInGround() )
        {
            for (int i = 0; i < 4; i++)
            {
                this.world.getWorld().spawnParticle(Particle.DRIP_LAVA, this.locX + this.motX * i / 4.0D, this.locY + this.motY * i / 4.0D, this.locZ + this.motZ * i / 4.0D, 2, 0, 0, 0, 0, null);
            }
        }
    }
    
    @Override
    protected void a(EntityLiving entityliving)
    {
        makeExplosion();
    }
    
    private void makeExplosion()
    {
        world.getWorld().createExplosion(locX, locY - 1, locZ, 3, true, false);
    }
}
