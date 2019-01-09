package pl.arieals.minigame.goldhunter.abilities;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import org.slf4j.Logger;

import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.arena.ArenaBuilder;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.utils.Direction;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class WallAbility implements AbilityHandler
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    @Inject
    private static ArenaBuilder arenaBuilder;
    
    @Override
    public boolean onUse(GoldHunterPlayer player, Location target)
    {
        if ( target == null )
        {
            Vector dir = player.getPlayer().getLocation().getDirection().multiply(5);
            target = player.getPlayer().getEyeLocation().add(dir);
        }
        
        Direction direction = Direction.fromYaw(player.getMinecraftPlayer().yaw);
        
        switch ( player.getShopItemLevel("vip.defender.ability") )
        {
        case 0:
            buildWall(target, direction, 5, 4, 1);
            break;
            
        case 1:
            buildWall(target, direction, 7, 5, 1);
            break;
            
        case 2:
            buildWall(target, direction, 7, 5, 2);
            break;
            
        default:
            throw new IllegalStateException();
        }
        
        return true;
    }
    
    private void buildWall(Location loc, Direction direction, int width, int height, int depth)
    {
        Block base = loc.getBlock().getRelative(direction.turnLeft().getBlockFace(), width / 2);
        
        boolean cobble = false;
        for ( int i = 0; i < depth; i++, cobble = !cobble )
        {
            Block base2 = base.getRelative(direction.getBlockFace(), i);
            boolean cobble2 = cobble;
            for ( int j = 0; j < height; j++, cobble2 = !cobble2 )
            {
                Block base3 = base2.getRelative(BlockFace.UP, j);
                boolean cobble3 = cobble2;
                for ( int k = 0; k < width; k++, cobble3 = !cobble3 )
                {
                    Block current = base3.getRelative(direction.turnRight().getBlockFace(), k);
                    
                    if ( current.getType() != Material.AIR )
                    {
                        continue;
                    }
                    
                    arenaBuilder.tryBuild(current, cobble3 ? Material.COBBLESTONE : Material.WOOD).whenSuccess(() -> playWallEffect(current, direction));
                }
            }
        }
    }
    
    private void playWallEffect(Block block, Direction direction)
    {
        double offX = ThreadLocalRandom.current().nextGaussian() * 0.13;
        double offY = Math.abs(ThreadLocalRandom.current().nextGaussian()) * 0.13;
        double offZ = ThreadLocalRandom.current().nextGaussian() * 0.13;
        
        Location front = direction.oposite().translateLocation(block.getLocation().add(0.5, 0.196, 0.5), 0.5);
        Location behind = direction.translateLocation(block.getLocation().add(0.5, 0.196, 0.5), 0.5);
        
        block.getWorld().spawnParticle(Particle.SMOKE_LARGE, front.getX(), front.getY(), front.getZ(), 1, offX, offY, offZ, 0, null);
        block.getWorld().spawnParticle(Particle.SMOKE_LARGE, behind.getX(), behind.getY(), behind.getZ(), 1, offX, offY, offZ, 0, null);
    }
}
