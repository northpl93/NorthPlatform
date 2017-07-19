package pl.north93.zgame.api.bukkit.gui;

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
}
