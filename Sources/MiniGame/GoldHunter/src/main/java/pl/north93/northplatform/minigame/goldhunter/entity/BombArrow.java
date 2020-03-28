package pl.north93.northplatform.minigame.goldhunter.entity;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

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
            /*for (int i = 0; i < 4; i++)
            {
                this.world.getWorld().spawnParticle(Particle.DRIP_LAVA, this.locX + this.motX * i / 4.0D, this.locY + this.motY * i / 4.0D, this.locZ + this.motZ * i / 4.0D, 2, 0, 0, 0, 0, null);
            }*/
            
            for (int i = 0; i < 10; i++)
            {
                for ( int j = 0; j < 4; j++ )
                {
                    double offsetX = random.nextGaussian() * 0.111997;
                    double offsetY = random.nextGaussian() * 0.111997;
                    double offsetZ = random.nextGaussian() * 0.111997;
                    
                    double x = locX + motX * i / 4 + offsetX;
                    double y = locY + motY * i / 4 + offsetY;
                    double z = locZ + motZ * i / 4 + offsetZ;
                    
                    double color = Math.max(Float.MIN_VALUE, Math.abs(random.nextFloat() % 0.1696969));
                    
                    this.world.getWorld().spawnParticle(Particle.REDSTONE, x, y, z, 0, 1, color, color, 1, null);
                }
            }
        }
    }
    
    @Override
    protected void onHitPlayer(GoldHunterPlayer player)
    {
        makeExplosion();
    }
    
    private void makeExplosion()
    {
        //world.getWorld().createExplosion(locX, locY - 1, locZ, 3, false, true);
        world.createExplosion(this, locX, locY - 1, locZ, 2.069f, false, true);
    }
}
