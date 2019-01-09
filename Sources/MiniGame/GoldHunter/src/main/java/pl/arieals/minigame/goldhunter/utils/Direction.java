package pl.arieals.minigame.goldhunter.utils;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public enum Direction
{
    SOUTH,
    WEST,
    NORTH,
    EAST,
    ;
    
    public Direction turnRight()
    {
        switch ( this )
        {
        case SOUTH: return EAST;
        case EAST: return NORTH;
        case NORTH: return WEST;
        case WEST: return SOUTH;
        }
        
        throw new RuntimeException();
    }
    
    public Direction turnLeft()
    {
        switch ( this )
        {
        case SOUTH: return WEST;
        case WEST: return NORTH;
        case NORTH: return EAST;
        case EAST: return SOUTH;
        }
        
        throw new RuntimeException();
    }
    
    public Direction oposite()
    {
        switch ( this )
        {
        case SOUTH: return NORTH;
        case WEST: return EAST;
        case NORTH: return SOUTH;
        case EAST: return WEST;
        }
        
        throw new RuntimeException();
    }
    
    public BlockFace getBlockFace()
    {
        return BlockFace.valueOf(name());
    }
    
    public Location translateLocation(Location location, double offset)
    {
        BlockFace face = getBlockFace();
        return location.add(offset * face.getModX(), 0, offset * face.getModZ());
    }
    
    public static Direction fromYaw(float yaw)
    {
        yaw += 360;
        yaw %= 360;
        
        if ( yaw >= 45 && yaw < 135 )
        {
            return WEST;
        }
        else if ( yaw >= 135 && yaw < 225 )
        {
            return NORTH;
        }
        else if ( yaw >= 225 && yaw < 315 )
        {
            return EAST;
        }
        else
        {
            return SOUTH;
        }
    }
}
