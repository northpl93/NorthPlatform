package pl.north93.northplatform.worldproperties.impl;

import org.bukkit.entity.Player;

import pl.north93.northplatform.worldproperties.IPlayerProperties;
import pl.north93.northplatform.worldproperties.IWorldProperties;
import pl.north93.northplatform.worldproperties.IWorldPropertiesManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class PlayerPropertiesImpl implements IPlayerProperties
{
    @Inject
    private static IWorldPropertiesManager propertiesManager;
    
    private final Player player;
    
    private boolean canBypassRestrictions;
    private boolean godMode;
    
    PlayerPropertiesImpl(Player player)
    {
        this.player = player;
    }
    
    @Override
    public Player getPlayer()
    {
        return player;
    }
    
    @Override
    public IWorldProperties getCurrentWorldProperties()
    {
        return propertiesManager.getProperties(player.getWorld());
    }

    @Override
    public boolean canBypassRestrictions()
    {
        return canBypassRestrictions;
    }

    @Override
    public boolean getGodMode()
    {
        return godMode;
    }

    @Override
    public void setCanBypassRestriction(boolean flag)
    {
        canBypassRestrictions = flag;
    }

    @Override
    public void setGodMode(boolean flag)
    {
        godMode = flag;
    }

    @Override
    public boolean effectiveCanBuild()
    {
        return canBypassRestrictions || getCurrentWorldProperties().isBuildAllowed();
    }

    @Override
    public boolean effectiveCanInteract()
    {
        return canBypassRestrictions || getCurrentWorldProperties().isInterractAllowed();
    }

    @Override
    public boolean effectiveInvulnerable()
    {
        return godMode || getCurrentWorldProperties().arePlayersInvulnerable();
    }
    
    @Override
    public boolean effectiveHunger()
    {
        return !godMode && getCurrentWorldProperties().isHungerEnabled();
    }
}
