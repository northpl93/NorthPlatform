package pl.arieals.minigame.bedwars.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.shop.upgrade.IUpgrade;

public class UpgradeInstallEvent extends BedWarsPlayerArenaEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final Team     team;
    private final IUpgrade upgrade;
    private final int      level;
    private final boolean  isInstalling;
    private       boolean  cancelled;

    public UpgradeInstallEvent(final LocalArena arena, final Team team, final Player issuer, final IUpgrade upgrade, final int level, final boolean isInstalling)
    {
        super(arena, issuer);
        this.team = team;
        this.upgrade = upgrade;
        this.level = level;
        this.isInstalling = isInstalling;
    }

    public Team getTeam()
    {
        return this.team;
    }

    public Player getIssuer()
    {
        return this.getPlayer();
    }

    public IUpgrade getUpgrade()
    {
        return this.upgrade;
    }

    public int getLevel()
    {
        return this.level;
    }

    public boolean isInstalling()
    {
        return this.isInstalling;
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("team", this.team).append("upgrade", this.upgrade).append("level", this.level).append("isInstalling", this.isInstalling).append("cancelled", this.cancelled).toString();
    }
}
