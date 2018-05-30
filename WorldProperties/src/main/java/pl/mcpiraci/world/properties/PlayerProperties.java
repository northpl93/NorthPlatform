package pl.mcpiraci.world.properties;

import org.bukkit.entity.Player;

public interface PlayerProperties
{
    Player getPlayer();
    
    WorldProperties getCurrentWorldProperties();
    
    boolean canBypassRestrictions();
    
    boolean getGodMode();
    
    void setCanBypassRestriction(boolean flag);
    
    void setGodMode(boolean flag);
    
    boolean effectiveCanBuild();
    
    boolean effectiveCanInteract();
    
    boolean effectiveInvulnerable();
}
