package pl.arieals.minigame.bedwars.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaEvent;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.arena.upgrade.IUpgrade;

public class UpgradeInstallEvent extends ArenaEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final Team     team;
    private final Player   issuer;
    private final IUpgrade upgrade;
    private       boolean  cancelled;

    public UpgradeInstallEvent(final LocalArena arena, final Team team, final Player issuer, final IUpgrade upgrade)
    {
        super(arena);
        this.team = team;
        this.issuer = issuer;
        this.upgrade = upgrade;
    }

    public Team getTeam()
    {
        return this.team;
    }

    public Player getIssuer()
    {
        return this.issuer;
    }

    public IUpgrade getUpgrade()
    {
        return this.upgrade;
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean b)
    {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
