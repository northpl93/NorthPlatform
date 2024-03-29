package pl.north93.northplatform.worldproperties;

import org.bukkit.entity.Player;

public interface IPlayerProperties
{
    Player getPlayer();
    
    IWorldProperties getCurrentWorldProperties();
    
    boolean canBypassRestrictions();
    
    boolean getGodMode();
    
    void setCanBypassRestriction(boolean flag);
    
    void setGodMode(boolean flag);
    
    boolean effectiveCanBuild();
    
    boolean effectiveCanInteract();
    
    boolean effectiveInvulnerable();
    
    boolean effectiveHunger();
}
