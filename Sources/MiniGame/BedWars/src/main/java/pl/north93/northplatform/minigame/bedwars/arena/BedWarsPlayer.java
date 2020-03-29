package pl.north93.northplatform.minigame.bedwars.arena;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getPlayerStatus;


import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.shared.api.PlayerStatus;
import pl.north93.northplatform.minigame.bedwars.event.PlayerEliminatedEvent;
import pl.north93.northplatform.minigame.bedwars.shop.elimination.IEliminationEffect;
import pl.north93.northplatform.minigame.bedwars.utils.TeamArmorUtils;

public class BedWarsPlayer
{
    private final INorthPlayer bukkitPlayer;
    private final IEliminationEffect eliminationEffect;
    private Team team;
    private boolean eliminated;
    private int kills;
    private int lives; // amount of additional lives

    public BedWarsPlayer(final INorthPlayer bukkitPlayer, final IEliminationEffect eliminationEffect)
    {
        this.bukkitPlayer = bukkitPlayer;
        this.eliminationEffect = eliminationEffect;
    }

    public boolean isOnline()
    {
        return ! this.isOffline();
    }

    /**
     * Sprawdza czy ten BedWarsPlayer jest offline.
     *
     * @return True jesli gracz jest offline, nie ma go na arenie.
     */
    public boolean isOffline()
    {
        final BedWarsPlayer bedWarsPlayer = this.bukkitPlayer.getPlayerData(BedWarsPlayer.class);
        if (this.bukkitPlayer.isOnline())
        {
            return this != bedWarsPlayer;
        }

        return true;
    }

    /**
     * Sprawdza czy gracz jest online i ma status {@link PlayerStatus#PLAYING}.
     * @return True jesli gracz jest online i aktywnie uczestniczy w grze (nie jest spectatorem).
     */
    public boolean isOnlineAndPlaying()
    {
        final PlayerStatus playerStatus = getPlayerStatus(this.bukkitPlayer);
        return playerStatus != null && ! this.isOffline() && playerStatus == PlayerStatus.PLAYING;
    }

    public INorthPlayer getBukkitPlayer()
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
            this.team.getPlayers().remove(this);
        }
        this.team = team;
        team.getPlayers().add(this);
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
        Bukkit.getPluginManager().callEvent(new PlayerEliminatedEvent(this.team.getArena(), this));
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
