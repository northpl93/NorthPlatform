package pl.arieals.minigame.bedwars.arena;

import org.bukkit.entity.Player;

public class BedWarsPlayer
{
    private final Player bukkitPlayer;
    private Team    team;
    private boolean alive;

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
        this.bukkitPlayer.sendMessage("Dolaczono do zespolu " + team.getColor() + team.getColor().name());
    }

    public boolean isAlive()
    {
        return this.alive;
    }

    public void setAlive(final boolean alive)
    {
        this.alive = alive;
    }
}
