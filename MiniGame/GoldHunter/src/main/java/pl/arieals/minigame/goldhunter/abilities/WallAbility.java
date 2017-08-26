package pl.arieals.minigame.goldhunter.abilities;

import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import pl.arieals.minigame.goldhunter.AbilityHandler;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class WallAbility implements AbilityHandler
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    private static final int WALL_WIDTH = 3;
    private static final int WALL_HEIGHT = 4;
    private static final int WALL_DEPTH = 2;
    
    @Override
    public boolean onUse(GoldHunterPlayer player, Location target)
    {
        if ( target == null )
        {
            Vector dir = player.getPlayer().getLocation().getDirection();
            dir.multiply(5);
            
            target = player.getPlayer().getEyeLocation().add(dir);
        }
        
        buildWall(player.getPlayer().getLocation().getDirection(), target);
        return true;
    }
    
    private void buildWall(Vector lookDirection, Location target)
    {
        Vector min;
        Vector max;
        
        lookDirection.setY(0).normalize();
        
        double sign = Math.signum(lookDirection.getX() / lookDirection.length());
        double lookDeg;
        if ( sign == 0 && lookDirection.getZ() > 0 )
        {
            lookDeg = 0;
        }
        else if ( sign == 0 && lookDirection.getZ() < 0 )
        {
            lookDeg = 180;
        }
        else
        {
            lookDeg = sign * Math.toDegrees(lookDirection.angle(new Vector(0, 0, 1)));
        }
        
        if ( lookDeg >= -45 && lookDeg < 45 )
        {
            min = new Vector(-WALL_WIDTH, 1, 0);
            max = new Vector(WALL_WIDTH + 1, WALL_HEIGHT + 1, WALL_DEPTH);
        }
        else if ( lookDeg >= 45 && lookDeg < 135 )
        {
            min = new Vector(-WALL_DEPTH + 1, 1, -WALL_WIDTH);
            max = new Vector(1, WALL_HEIGHT + 1, WALL_WIDTH + 1);
        }
        else if ( lookDeg >= -135 && lookDeg < -45 )
        {
            min = new Vector(WALL_DEPTH, 1, -WALL_WIDTH);
            max = new Vector(0, WALL_HEIGHT + 1, WALL_WIDTH + 1);
        }
        else
        {
            min = new Vector(-WALL_WIDTH - 1, 1, -WALL_DEPTH);
            max = new Vector(WALL_WIDTH, WALL_HEIGHT + 1, 0);
        }
        
        buildCuboid(target, min, max);
    }
    
    private void buildCuboid(Location target, Vector min, Vector max)
    {
        logger.debug("min: {}, max: {}", min, max);
        
        boolean cobble = false;
        for ( int y = min.getBlockY(); y < max.getBlockY(); y++, cobble = !cobble )
        {
            boolean cobble2 = cobble;
            for ( int z = min.getBlockZ(); z < max.getBlockZ(); z++ )
            {
                boolean cobble3 = cobble2;
                for ( int x = min.getBlockX(); x < max.getBlockX(); x++ )
                {
                    target.clone().add(x, y, z).getBlock().setType(cobble3 ? Material.COBBLESTONE : Material.WOOD);
                    // TODO: add effect
                    cobble3 = !cobble3;
                }
                cobble2 = !cobble2;
            }
        }
    }
}
