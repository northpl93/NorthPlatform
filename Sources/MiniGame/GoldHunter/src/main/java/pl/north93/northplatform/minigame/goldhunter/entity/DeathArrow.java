package pl.north93.northplatform.minigame.goldhunter.entity;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import pl.north93.northplatform.minigame.goldhunter.effect.DeathEffect;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

public class DeathArrow extends SpecialArrow
{
    public DeathArrow(World world, LivingEntity shooter)
    {
        super(world, shooter);
    }

    @Override
    protected void onTick()
    {
        spawnParticle();
    }
    
    private void spawnParticle()
    {
        if ( !isInGround() )
        {
            for (int i = 0; i < 4; i++)
            {
                for ( int j = 0; j < 10; j++ )
                {
                    double offsetX = random.nextGaussian() * 0.111997;
                    double offsetY = random.nextGaussian() * 0.111997;
                    double offsetZ = random.nextGaussian() * 0.111997;
                    
                    double x = locX + motX * i / 4 + offsetX;
                    double y = locY + motY * i / 4 + offsetY;
                    double z = locZ + motZ * i / 4 + offsetZ;
                    
                    double color = Math.max(Float.MIN_VALUE, Math.abs(random.nextFloat() % 0.1696969));
                    this.world.getWorld().spawnParticle(Particle.REDSTONE, x, y, z, 0, color, color, color, 1, null);
                }
            }
        }
        else
        {
            int count = random.nextInt(3);
            Vector dir = getBukkitEntity().getLocation().getDirection().multiply(1/3.0).multiply(new Vector(1, 1, -1));
            
            for ( int i = 0; i < count; i++ )
            {
                double x = locX + dir.getX() + random.nextGaussian() * 0.22;
                double y = locY + dir.getY() + random.nextGaussian() * 0.22;
                double z = locZ + dir.getZ() + random.nextGaussian() * 0.22;
                
                double color = Math.max(Float.MIN_VALUE, Math.abs(random.nextFloat() % 0.1696969));
                
                this.world.getWorld().spawnParticle(Particle.REDSTONE, x, y, z, 0, color, color, color, 1, null);
            }
        }
    }
    
    @Override
    protected void onHitPlayer(GoldHunterPlayer player)
    {
        player.getEffectTracker().addEffect(new DeathEffect());
    }
}
