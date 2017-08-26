package pl.arieals.minigame.goldhunter;

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
    
    public BlockFace getBlockFace()
    {
        return BlockFace.valueOf(name());
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
