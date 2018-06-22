package pl.arieals.minigame.goldhunter.entity;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

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
                double offsetX = random.nextGaussian() * 0.09;
                double offsetY = random.nextGaussian() * 0.09;
                double offsetZ = random.nextGaussian() * 0.09;
                
                this.world.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, this.locX + this.motX * i / 4.0, this.locY + this.motY * i / 4.0, this.locZ + this.motZ * i / 4.0, 2, offsetX, offsetY, offsetZ, 0, null);
            }
        }
        else if ( MinecraftServer.currentTick % 20 == 0 )
        {
            double offsetX = random.nextGaussian() * 0.22;
            double offsetY = random.nextGaussian() * 0.22;
            double offsetZ = random.nextGaussian() * 0.22;
            
            this.world.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, locX, locY, locZ, 1, offsetX, offsetY, offsetZ, 0, null);
        }
        
        System.out.println("");
    }
    
    @Override
    protected void onHitPlayer(GoldHunterPlayer player)
    {
        // TODO Auto-generated method stub
    }
}
