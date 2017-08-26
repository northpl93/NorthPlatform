package pl.arieals.minigame.goldhunter.entity;

import org.apache.logging.log4j.Logger;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_10_R1.EntityLiving;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.effect.PoisonEffect;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PoisonArrow extends HomingArrow
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    @Inject
    private static GoldHunter goldHunter;
    
    public PoisonArrow(World world, LivingEntity shooter, LivingEntity target)
    {
        super(world, shooter, target);
    }
    
    @Override
    protected void onTick()
    {
        super.onTick();
        
        for (int i = 0; i < 4; i++)
        {
            this.world.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, this.locX + this.motX * i / 4.0D, this.locY + this.motY * i / 4.0D, this.locZ + this.motZ * i / 4.0D, 10, 0, 0, 0, 0, null);
            //this.world.addParticle(EnumParticle.VILLAGER_HAPPY, this.locX + this.motX * i / 4.0D, this.locY + this.motY * i / 4.0D, this.locZ + this.motZ * i / 4.0D, 0, 0, 0);
        }
    }
    
    @Override
    protected void a(EntityLiving entityliving)
    {
        super.a(entityliving);
        
        if ( !( entityliving instanceof EntityPlayer ) )
        {
            return;
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer((Player) entityliving.getBukkitEntity());
        
        logger.debug("Poison arrow hit in player {}", player);
        if ( player != null && player.isIngame() )
        {
            player.getEffectTracker().addEffect(new PoisonEffect(), 200);
        }
    }
}
