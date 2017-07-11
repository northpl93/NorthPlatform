package pl.arieals.api.minigame.server.gamehost.deathmatch;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchFightStartEvent;
import pl.north93.zgame.api.bukkit.utils.AbstractCountdown;

/**
 * Task zarządzający startem walki na deathmatchu. Implementuje
 * {@link IFightManager}, a sama implementacja jest ukryta.
 */
public class FightStartCountdown extends AbstractCountdown implements IFightManager
{
    private final LocalArena arena;
    private boolean fight;

    public FightStartCountdown(final LocalArena arena)
    {
        super(20);
        this.arena = arena;
    }

    @Override
    public boolean isFightStarted()
    {
        return this.fight;
    }

    @Override
    public int getTimeToStart()
    {
        return this.getTime();
    }

    @Override
    protected void loop(final int time)
    {

    }

    @Override
    protected void end()
    {
        this.fight = true; // oznaczamy rozpoczeta walke
        Bukkit.getPluginManager().callEvent(new DeathMatchFightStartEvent(this.arena));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("fight", this.fight).toString();
    }
}
