package pl.arieals.minigame.bedwars.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.bedwars.event.PlayerEliminatedEvent;
import pl.arieals.minigame.bedwars.shop.elimination.IEliminationEffect;
import pl.arieals.minigame.bedwars.utils.TeamArmorUtils;

public class BedWarsPlayer
{
    private final Player             bukkitPlayer;
    private final IEliminationEffect eliminationEffect;
    private Team    team;
    private boolean eliminated;
    private int     kills;
    private int     lives; // ilosc dodatkowych zyc

    public BedWarsPlayer(final Player bukkitPlayer, final IEliminationEffect eliminationEffect)
    {
        this.bukkitPlayer = bukkitPlayer;
        this.eliminationEffect = eliminationEffect;
    }

    public Player getBukkitPlayer()
    {
        return this.bukkitPlayer;
    }

    public IEliminationEffect getEliminationEffect()
    {
        return this.eliminationEffect;
    }

    public Team getTeam()
    {
        return this.team;
    }

    public void switchTeam(final Team team)
    {
        if (this.team != null)
        {
            this.team.getPlayers().remove(this.bukkitPlayer);
        }
        this.team = team;
        team.getPlayers().add(this.bukkitPlayer);
        this.bukkitPlayer.teleport(team.getSpawn());
        TeamArmorUtils.updateArmor(this.bukkitPlayer, team);
    }

    public boolean isEliminated()
    {
        return this.eliminated;
    }

    public void eliminate()
    {
        if (this.eliminated)
        {
            return;
        }
        this.eliminated = true;
        Bukkit.getPluginManager().callEvent(new PlayerEliminatedEvent(this.team.getArena(), this.bukkitPlayer));
    }

    public int getKills()
    {
        return this.kills;
    }

    public void incrementKills()
    {
        this.kills++;
    }

    public int getLives()
    {
        return this.lives;
    }

    public void addLive()
    {
        this.lives++;
    }

    public void removeLife()
    {
        this.lives--;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("bukkitPlayer", this.bukkitPlayer).append("team", this.team).append("eliminated", this.eliminated).append("kills", this.kills).append("lives", this.lives).toString();
    }
}
