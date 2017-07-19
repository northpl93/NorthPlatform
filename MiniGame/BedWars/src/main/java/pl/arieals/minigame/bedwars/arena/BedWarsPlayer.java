package pl.arieals.minigame.bedwars.arena;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.bedwars.utils.TeamArmorUtils;

public class BedWarsPlayer
{
    private final Player bukkitPlayer;
    private Team    team;
    private boolean alive;
    private int     kills;

    public BedWarsPlayer(final Player bukkitPlayer)
    {
        this.bukkitPlayer = bukkitPlayer;
        this.alive = true;
    }

    public Player getBukkitPlayer()
    {
        return this.bukkitPlayer;
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
        //this.bukkitPlayer.sendMessage("Dolaczono do zespolu " + team.getColor() + team.getColor().name());
    }

    public boolean isAlive()
    {
        return this.alive;
    }

    public void setAlive(final boolean alive)
    {
        this.alive = alive;
    }

    public int getKills()
    {
        return this.kills;
    }

    public void incrementKills()
    {
        this.kills++;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("bukkitPlayer", this.bukkitPlayer).append("team", this.team).append("alive", this.alive).append("kills", this.kills).toString();
    }
}
