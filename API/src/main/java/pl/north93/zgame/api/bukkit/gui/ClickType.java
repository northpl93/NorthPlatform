package pl.north93.zgame.api.bukkit.gui;

import org.bukkit.event.block.Action;

public enum ClickType
{
    LEFT,
    RIGHT,
    ;
    
    public static ClickType fromBukkitClickType(org.bukkit.event.inventory.ClickType type)
    {
        if ( type.isLeftClick() )
        {
            return LEFT;
        }
        if ( type.isRightClick() )
        {
            return RIGHT;
        }
        
        return null;
    }
    
    public static ClickType fromBukkitAction(Action action)
    {
        if ( action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK )
        {
            return LEFT;
        }
        if ( action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK )
        {
            return RIGHT;
        }
        
        return null;
    }
}
